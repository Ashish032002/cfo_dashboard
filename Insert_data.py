
import pandas as pd
import mysql.connector


db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'Schooldav@123',
    'database': 'cfo_dashboard',
    'allow_local_infile': True
}




csv_file = r"C:\Users\ASHISH\OneDrive\Desktop\streamlit_cfo\cfo_dashboard_sample_data.csv"
excel_file1 = r"C:\Users\ASHISH\OneDrive\Desktop\streamlit_cfo\sample_budgeting_planning_data.xlsx"
excel_file2 = r"C:\Users\ASHISH\OneDrive\Desktop\streamlit_cfo\sample_profitability_data - V1.xlsx"


conn = mysql.connector.connect(**db_config)
cursor = conn.cursor()

try:
    print("Connected to MySQL...\n")

    
    print("Uploading CSV...")
    df_csv = pd.read_csv(csv_file)
    table_name = "cfo_dashboard_sample_data"

   
    columns = ', '.join([f"`{col}` TEXT" for col in df_csv.columns])
    cursor.execute(f"CREATE TABLE IF NOT EXISTS `{table_name}` ({columns});")

    
    placeholders = ', '.join(['%s'] * len(df_csv.columns))
    insert_sql = f"INSERT INTO `{table_name}` VALUES ({placeholders})"
    cursor.executemany(insert_sql, df_csv.values.tolist())
    print(f"Inserted CSV data into {table_name}.\n")

    
    print("Uploading Excel Budgeting Sheets...")
    excel1 = pd.read_excel(excel_file1, sheet_name=None)
    for sheet_name, df in excel1.items():
        table_name = f"budgeting_{sheet_name.lower().replace(' ', '_')}"
        columns = ', '.join([f"`{col}` TEXT" for col in df.columns])
        cursor.execute(f"CREATE TABLE IF NOT EXISTS `{table_name}` ({columns});")
        placeholders = ', '.join(['%s'] * len(df.columns))
        insert_sql = f"INSERT INTO `{table_name}` VALUES ({placeholders})"
        cursor.executemany(insert_sql, df.values.tolist())
        print(f"Inserted data into {table_name}.")

    print("\nUploading Excel Profitability Sheets...")
    excel2 = pd.read_excel(excel_file2, sheet_name=None)
    for sheet_name, df in excel2.items():
        table_name = f"profitability_{sheet_name.lower().replace(' ', '_')}"
        columns = ', '.join([f"`{col}` TEXT" for col in df.columns])
        cursor.execute(f"CREATE TABLE IF NOT EXISTS `{table_name}` ({columns});")
        placeholders = ', '.join(['%s'] * len(df.columns))
        insert_sql = f"INSERT INTO `{table_name}` VALUES ({placeholders})"
        cursor.executemany(insert_sql, df.values.tolist())
        print(f"Inserted data into {table_name}.")

    conn.commit()
    print("\n All data inserted successfully using cursor!")

except mysql.connector.Error as err:
    print(f"Error: {err}")
    conn.rollback()
finally:
    cursor.close()
    conn.close()
    print("MySQL connection closed.")
