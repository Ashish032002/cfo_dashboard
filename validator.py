import pandas as pd
from rapidfuzz import fuzz, process
from db_config import get_db_connection

engine = get_db_connection()
print("Connected to database.")

# Load master_data
master_df = pd.read_sql("SELECT division_name, pincode, state_name FROM master_data;", engine)

# Load pincode_master and abbreviation_list
pincode_df = pd.read_sql("SELECT city, pincode, state FROM pincode_master;", engine)
abbrev_df = pd.read_sql("SELECT state_abbreviation, state FROM abbreviation_list;", engine)

# Map abbreviation to full state name in pincode_master
pincode_df = pincode_df.merge(abbrev_df, left_on='state', right_on='state_abbreviation', how='left')
# Use full state name from abbreviation_list (if available)
pincode_df['state_name'] = pincode_df['state_y'].combine_first(pincode_df['state_x'])
pincode_df.rename(columns={'city': 'division_name'}, inplace=True)

# Select columns and drop duplicates & missing
pincode_df = pincode_df[['division_name', 'pincode', 'state_name']].drop_duplicates()
pincode_df.dropna(subset=['division_name', 'pincode', 'state_name'], inplace=True)

# Combine master_data and pincode_master into one master dataset
combined_master = pd.concat([master_df, pincode_df], ignore_index=True).drop_duplicates()
combined_master.dropna(subset=['division_name', 'state_name', 'pincode'], inplace=True)

print(f"Loaded {len(combined_master)} combined master rows.")

# Load input addresses
input_df = pd.read_sql("SELECT * FROM input_addresses;", engine)
print(f"Loaded {len(input_df)} input addresses.")

# Prepare a dictionary to map abbreviations to full state names for input cleaning
abbrev_map = dict(zip(abbrev_df['state_abbreviation'].str.upper(), abbrev_df['state']))

def fuzzy_match(value, choices):
    if pd.isna(value) or str(value).strip() == "":
        return None, 0
    match = process.extractOne(str(value), choices, scorer=fuzz.token_sort_ratio)
    if match:
        return match[0], match[1]  # Only return label and score
    else:
        return None, 0


def compute_confidence(pin_score, city_score, state_score):
    score = (0.4 * pin_score + 0.3 * city_score + 0.3 * state_score)
    if score >= 0.85:
        return "High"
    elif score >= 0.6:
        return "Medium"
    elif score >= 0.4:
        return "Low"
    else:
        return "Rejected"

validated = []

for _, row in input_df.iterrows():
    city_in = str(row.get("city", "")).strip()
    state_in = str(row.get("state", "")).strip()
    pincode_in = str(row.get("pincode", "")).strip()

    # Normalize input state abbreviation if present
    state_upper = state_in.upper()
    if state_upper in abbrev_map:
        state_in_full = abbrev_map[state_upper]
    else:
        state_in_full = state_in

    result = row.to_dict()
    result.update({
        "division_db": None,
        "state_db": None,
        "confidence": "Rejected",
        "flag": None
    })

    pin_score = city_score = state_score = 0

    # First try exact pincode match on combined master
    if pincode_in.isdigit():
        match = combined_master[combined_master["pincode"] == int(pincode_in)]

        if not match.empty:
            division = match.iloc[0]["division_name"]
            state_db = match.iloc[0]["state_name"]

            division_str = str(division).strip().lower()
            state_db_str = str(state_db).strip().lower()
            city_in_str = city_in.lower()
            state_in_str = state_in_full.lower()

            city_score = fuzz.token_sort_ratio(city_in_str, division_str) / 100
            state_score = fuzz.token_sort_ratio(state_in_str, state_db_str) / 100
            pin_score = 1

            result.update({"division_db": division, "state_db": state_db})

            if city_score < 0.6 or state_score < 0.6:
                result["flag"] = "PIN_MISMATCH"
        else:
            result["flag"] = "INVALID_PIN"
    else:
        # Fuzzy match city (division_name) on combined master
        best_div, score = fuzzy_match(city_in, combined_master["division_name"].unique())

        if best_div:
            possible_states = combined_master[combined_master["division_name"] == best_div]["state_name"].unique()
            # Normalize possible states for comparison
            possible_states_lower = [str(s).lower() for s in possible_states]

            if state_in_full.lower() in possible_states_lower:
                city_score = score / 100
                state_score = 1
                result.update({"division_db": best_div, "state_db": state_in_full})
            else:
                result["flag"] = "AMBIGUOUS_CITY"
        else:
            result["flag"] = "CITY_NOT_FOUND"

    result["confidence"] = compute_confidence(pin_score, city_score, state_score)
    validated.append(result)

validated_df = pd.DataFrame(validated)

validated_df.to_excel("validated_output.xlsx", index=False)
print(f"Validation complete. Saved {len(validated_df)} rows to 'validated_output.xlsx'.")
 