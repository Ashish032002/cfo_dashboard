import os
from openai import OpenAI
from dotenv import load_dotenv
from schema_config import SCHEMA_CONTEXT  
import re

load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def strip_sql_formatting(text): 
    return re.sub(r"```sql|```", "", text).strip()


def correct_sql_with_openai(user_question, broken_sql, error_message):
    prompt = f"""
You are an expert SQL debugger.

Schema:
{SCHEMA_CONTEXT}   

User Question:
{user_question}   

Broken SQL:
{broken_sql}

Error Message:
{error_message}

Please correct the SQL query based on the schema and user intent.
Only output the corrected SQL query.
"""
    try:
        messages = [
            {"role": "system", "content": "You are a helpful assistant that fixes incorrect SQL queries."},
            {"role": "user", "content": prompt}
        ]
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=messages,
            temperature=0
        )
        corrected_query = response.choices[0].message.content
        return strip_sql_formatting(corrected_query) 
    except Exception as e:
        return f"-- Failed to correct query: {e}"
