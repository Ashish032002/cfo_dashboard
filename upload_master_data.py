import pandas as pd
from db_config import get_db_connection

print("Starting upload_master_data.py...")
try:
    engine = get_db_connection()
    print("Database connection established.")
    file_path = "datasets/master_data.csv"
    df = pd.read_csv(file_path)
    print(f"Loaded {len(df)} rows from CSV.")
    # Keep only necessary columns
    df = df[['divisionname', 'pincode', 'statename']].copy()
    df.rename(columns={
    'divisionname': 'division_name',
    'pincode': 'pincode',
    'statename': 'state_name'
    }, inplace=True)
    # Clean
    df.drop_duplicates(inplace=True)
    df.dropna(subset=['division_name', 'pincode', 'state_name'], inplace=True)
    print(f" Cleaned data: {len(df)} valid rows remain.")
    # Upload
    df.to_sql('master_data', engine, if_exists='replace', index=False)
    print(f" Uploaded {len(df)} rows into 'master_data' table.")
except Exception as e:
    print(" Error:", e)