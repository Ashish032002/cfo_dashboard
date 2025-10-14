import pandas as pd
from db_config import get_db_connection

print("Starting upload_input_data.py")
try:
    engine = get_db_connection()
    print("Database connection established.")
    file_path = "datasets/input_addresses.xlsx"
    df = pd.read_excel(file_path)
    print(f"Loaded {len(df)} rows from Excel.")
    # Normalize column names
    df.columns = df.columns.str.strip().str.lower()
    expected = ['address1', 'address2', 'address3', 'city', 'state', 'pincode']
    df = df[[c for c in expected if c in df.columns]]
    # Clean
    df.drop_duplicates(inplace=True)
    print(f"Cleaned data: {len(df)} valid rows remain.")
    df.to_sql('input_addresses', engine, if_exists='replace', index=False)
    print(f"Uploaded {len(df)} rows into 'input_addresses' table.")
except Exception as e:
    print("Error:", e)