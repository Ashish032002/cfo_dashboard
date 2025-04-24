import streamlit as st
import pandas as pd
from query_utils import generate_sql_from_openai, run_sql_query
import seaborn as sns
import matplotlib.pyplot as plt


def analyze_data(df):
    categorical_cols, numerical_cols, time_series_cols = [], [], []
    for col in df.columns:
        if df[col].dtype == 'object':
            temp = pd.to_datetime(df[col], format="%Y-%m-%d", errors="coerce")
            if temp.notna().all():
                time_series_cols.append(col)
                continue

        if pd.api.types.is_numeric_dtype(df[col]):
            numerical_cols.append(col)
        elif df[col].nunique() < len(df) * 0.2:
            categorical_cols.append(col)

    if time_series_cols and numerical_cols:
        st.markdown(f"### 📈 Line Chart: `{time_series_cols[0]}` vs `{numerical_cols[0]}`")
        plot_line_chart(df, time_series_cols[0], numerical_cols[0])
    elif categorical_cols and numerical_cols:
        st.markdown(f"### 📊 Bar Chart: `{categorical_cols[0]}` vs `{numerical_cols[0]}`")
        plot_bar_chart(df, categorical_cols[0], numerical_cols[0])
    elif len(numerical_cols) == 1:
        st.markdown(f"### 📉 Histogram of `{numerical_cols[0]}`")
        plot_histogram(df, numerical_cols[0])
    elif len(numerical_cols) > 1:
        st.markdown(f"### 🔘 Scatter Plot: `{numerical_cols[0]}` vs `{numerical_cols[1]}`")
        plot_scatter_plot(df, numerical_cols[0], numerical_cols[1])
    else:
        st.info("Not enough numerical/categorical data for meaningful charts.")

def plot_bar_chart(df, cat_col, num_col):
    fig, ax = plt.subplots(figsize=(3.5, 1.8), dpi=100)
    sns.barplot(x=cat_col, y=num_col, data=df, ax=ax)
    plt.xticks(rotation=45)
    ax.tick_params(axis='both', labelsize=8)
    plt.tight_layout()
    col1, col2, col3 = st.columns([1, 2, 1])
    with col2:
        st.pyplot(fig, bbox_inches='tight', clear_figure=True)

def plot_line_chart(df, time_col, num_col):
    df[time_col] = pd.to_datetime(df[time_col], errors='coerce')
    fig, ax = plt.subplots(figsize=(3.5, 1.8), dpi=100)
    sns.lineplot(x=time_col, y=num_col, data=df, ax=ax)
    plt.xticks(rotation=45)
    ax.tick_params(axis='both', labelsize=8)
    plt.tight_layout()
    col1, col2, col3 = st.columns([1, 2, 1])
    with col2:
        st.pyplot(fig, bbox_inches='tight', clear_figure=True)

def plot_histogram(df, num_col):
    fig, ax = plt.subplots(figsize=(3.5, 1.8), dpi=100)
    sns.histplot(df[num_col], kde=True, ax=ax)
    ax.tick_params(axis='both', labelsize=8)
    plt.tight_layout()
    col1, col2, col3 = st.columns([1, 2, 1])
    with col2:
        st.pyplot(fig, bbox_inches='tight', clear_figure=True)

def plot_scatter_plot(df, num_col1, num_col2):
    fig, ax = plt.subplots(figsize=(3.5, 1.8), dpi=100)
    sns.scatterplot(x=num_col1, y=num_col2, data=df, ax=ax)
    ax.tick_params(axis='both', labelsize=8)
    plt.tight_layout()
    col1, col2, col3 = st.columns([1, 2, 1])
    with col2:
        st.pyplot(fig, bbox_inches='tight', clear_figure=True)


st.set_page_config(layout="wide")
st.markdown("""
    <style>
        html, body, [class*="css"]  {
            font-family: 'Segoe UI', sans-serif;
            background-color: #0e1117;
            color: #ffffff;
        }
        .stDataFrameContainer {
            border: 1px solid #444;
            border-radius: 8px;
            padding: 10px;
        }
        div.stButton > button:first-child {
            background-color: #00aaff;
            color: white;
            padding: 8px 20px;
            border-radius: 8px;
            border: none;
            font-weight: bold;
            transition: 0.3s;
        }
        div.stButton > button:first-child:hover {
            background-color: #0088cc;
            transform: scale(1.02);
        }
        .stTextInput > div > div > input {
            border-radius: 10px;
            padding: 10px;
            background-color: #1e222b;
            color: white;
        }
        .element-container:has(> .stPlotlyChart, .stAltairChart, .stPyplotChart) {
            margin-top: -30px;
            margin-bottom: 20px;
        }
        h1, h2, h3, h4 {
            color: #00ffaa;
        }
    </style>
""", unsafe_allow_html=True)

st.title("Text to SQL🛢")

query = st.text_input("Ask a question in natural language ", placeholder="E.g. Show average salary by department")


if st.button("Generate Query Only"):
    with st.spinner("Generating SQL..."):
        sql_query = generate_sql_from_openai(query)
        st.session_state["generated_sql"] = sql_query
        st.code(sql_query, language="sql")
        st.success("Query generated. You can now manually review and run it.")


if "generated_sql" in st.session_state:
    st.subheader("🛠️ Manually Review & Edit SQL Before Execution")
    edited_sql = st.text_area("Edit your SQL below if needed:", value=st.session_state["generated_sql"], height=200)

  
    if st.button("Run Final Query"):
        with st.spinner("Running SQL and fetching results..."):
            df = run_sql_query(edited_sql, original_question=query)

            if "Error" in df.columns:
                st.error("🚨 An error occurred while running the query:")
                st.code(df["Error"].iloc[0], language="text")

                if "Original_Error_Message" in df.columns:
                    st.markdown("### 🧱 Original SQL Error Message")
                    st.code(df["Original_Error_Message"].iloc[0], language="text")

                if "Correction_Attempted" in df.columns:
                    st.markdown("### 🔧 Correction Attempted SQL:")
                    st.code(df["Correction_Attempted"].iloc[0], language="sql")

            elif not df.empty:
                st.success("✅ Query ran successfully! Here's the result:")
                st.dataframe(df)

                if "Correction_Info" in df.columns:
                    st.markdown("### ✅ Auto-Corrected SQL from Error Recovery")
                    corrected_sql = df["Correction_Info"].iloc[0]
                    st.code(corrected_sql, language="sql")

                    st.subheader("✏️ Edit and Re-run Corrected SQL")
                    corrected_edit = st.text_area(
                        "The auto-corrected query can be edited below and re-run:",
                        value=corrected_sql,
                        height=200
                    )

                    if st.button("🔁 Re-run Corrected SQL"):
                        with st.spinner("Re-running corrected SQL..."):
                            re_df = run_sql_query(corrected_edit, original_question=query)

                            if "Error" in re_df.columns:
                                st.error("🚨 An error occurred while re-running the corrected SQL:")
                                st.code(re_df["Error"].iloc[0], language="text")

                                if "Original_Error_Message" in re_df.columns:
                                    st.markdown("### 🧱 Original SQL Error Message")
                                    st.code(re_df["Original_Error_Message"].iloc[0], language="text")

                                if "Correction_Attempted" in re_df.columns:
                                    st.markdown("### 🔧 Correction Attempted SQL:")
                                    st.code(re_df["Correction_Attempted"].iloc[0], language="sql")

                            elif not re_df.empty:
                                st.success("✅ Corrected query ran successfully!")
                                st.dataframe(re_df)
                                st.subheader("📊 Suggested Chart")
                                analyze_data(re_df)
                            else:
                                st.warning("⚠️ Corrected query returned no data.")

                if st.toggle("📄 Show original generated SQL"):
                    st.code(st.session_state["generated_sql"], language="sql")

                st.subheader("📊 Suggested Chart")
                analyze_data(df)

            else:
                st.warning("⚠️ Query returned no data.")

