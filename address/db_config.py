
from sqlalchemy import create_engine

def get_db_connection():
    engine = create_engine(
             "postgresql+psycopg2://postgres:123456789@localhost:5432/addressdb"
    )
    return engine  