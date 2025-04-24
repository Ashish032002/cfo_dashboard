import os
import re
import mysql.connector
import pandas as pd
from openai import OpenAI
from dotenv import load_dotenv
from sql_corrector import correct_sql_with_openai
from schema_config import SCHEMA_CONTEXT


load_dotenv()
client = OpenAI(api_key=st.secrets["OPENAI_API_KEY"]) # type: ignore
print("[DEBUG] Loaded API Key:", os.getenv("OPENAI_API_KEY"))


SYSTEM_PROMPT = "You are a helpful assistant that translates natural language questions into SQL queries."

def strip_sql_formatting(text):
    return re.sub(r"```sql|```", "", text).strip()

def generate_sql_from_openai(question: str) -> str:
    try:
        messages = [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": f"Schema:\n{SCHEMA_CONTEXT}\n\nQuestion:\n{question}"}
        ]
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=messages,
            temperature=0
        )
        return strip_sql_formatting(response.choices[0].message.content)
    except Exception as e:
        return f"-- Error generating SQL: {e}"

def run_sql_query(query: str, original_question: str = "") -> pd.DataFrame:
    try:
        conn = mysql.connector.connect(
            host=st.secrets["MYSQL_HOST"],
            user=st.secrets["MYSQL_USER"],
            password=st.secrets["MYSQL_PASSWORD"],
            database=st.secrets["MYSQL_DATABASE"]
        )
        df = pd.read_sql(query, conn)
        conn.close()
        return df
    except Exception as e:
        error_message = str(e)
        corrected_query = correct_sql_with_openai(original_question, query, error_message)
        print("[DEBUG] Attempting corrected query:", corrected_query)
        print(error_message)

        try:
            conn = mysql.connector.connect(
                host=os.getenv("MYSQL_HOST"),
                user=os.getenv("MYSQL_USER"),
                password=os.getenv("MYSQL_PASSWORD"),
                database=os.getenv("MYSQL_DATABASE")
            )
            df = pd.read_sql(corrected_query, conn)
            conn.close()
            df["Correction_Info"] = f"Auto-corrected Query:\n{corrected_query}"
            df["Original_Error_Message"] = error_message
            return df
        except Exception as e2:
            return pd.DataFrame({
                "Error": [f"Original error: {error_message}\n\nCorrection error: {str(e2)}"],
                "Correction_Attempted": [corrected_query],
                "Original_Error_Message": [error_message]
            })

