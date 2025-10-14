import pandas as pd
from db_config import get_db_connection

print("Starting upload_pincode_master.py...")

try:
    engine = get_db_connection()
    print("Database connection established.")

    # Load pincode_master.xlsx
    pin_df = pd.read_excel("datasets/pincode_master.xlsx")  # Reading Excel file
    pin_df.columns = pin_df.columns.str.strip().str.lower()
    pin_df.rename(columns={
        'city': 'city',
        'state': 'state',
        'pincode': 'pincode'
    }, inplace=True)
    pin_df.drop_duplicates(inplace=True)
    pin_df.dropna(subset=['city', 'pincode', 'state'], inplace=True)

    # Load abbreviation_list.csv (with encoding fix)
    abbrev_df = pd.read_csv("datasets/abbreviation_list.csv", encoding='ISO-8859-1')  # Encoding fix
    abbrev_df.columns = abbrev_df.columns.str.strip().str.lower()
    abbrev_df.rename(columns={
        'abbreviation': 'state_abbreviation',
        'state': 'state'
    }, inplace=True)
    abbrev_df.drop_duplicates(inplace=True)

    # Upload both to DB
    pin_df.to_sql("pincode_master", engine, if_exists="replace", index=False)
    abbrev_df.to_sql("abbreviation_list", engine, if_exists="replace", index=False)

    print(f"Uploaded {len(pin_df)} rows to 'pincode_master'.")
    print(f"Uploaded {len(abbrev_df)} rows to 'abbreviation_list'.")

except Exception as e:
    print("Error:", e)
