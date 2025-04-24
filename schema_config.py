SCHEMA_CONTEXT = {
    "tables": {
        "budgeting_sheet1": {
            "primary_key": ["Department", "Region", "Quarter", "Year"],
            "columns": {
                "Department": "TEXT",
                "Region": "TEXT",
                "Planned Budget ($M)": "FLOAT",
                "Actual Spend ($M)": "FLOAT",
                "Variance ($M)": "FLOAT",
                "Variance (%)": "FLOAT",
                "Quarter": "TEXT",
                "Year": "INTEGER"
            }
        },
        "profitability_product_profitability": {
            "primary_key": ["Product ID", "Region", "Month", "Year"],
            "columns": {
                "Product ID": "TEXT",
                "Region": "TEXT",
                "Month": "INTEGER",
                "Year": "INTEGER",
                "Quarter": "TEXT",
                "Product Name": "TEXT",
                "Category": "TEXT",
                "Channel": "TEXT",
                "Revenue ($K)": "FLOAT",
                "COGS ($K)": "FLOAT",
                "Units Sold": "INTEGER",
                "Customer Segment": "TEXT",
                "Gross Profit ($K)": "FLOAT",
                "Gross Margin (%)": "FLOAT"
            }
        },
        "cfo_dashboard_sample_data ": {
            "primary_key": ["Month", "Year"],
            "columns": {
                "Month": "INTEGER",
                "Year": "INTEGER",
                "Quarter": "TEXT",
                "Revenue ($M)": "FLOAT",
                "Cost of Goods Sold (COGS) ($M)": "FLOAT",
                "Operating Expenses ($M)": "FLOAT",
                "Cash Flow - Operating ($M)": "FLOAT",
                "Cash Flow - Investing ($M)": "FLOAT",
                "Cash Flow - Financing ($M)": "FLOAT",
                "Inventory ($M)": "FLOAT",
                "Accounts Receivable (Days)": "INTEGER",
                "Accounts Payable (Days)": "INTEGER",
                "Production Efficiency (%)": "FLOAT",
                "Gross Profit ($M)": "FLOAT",
                "EBITDA ($M)": "FLOAT",
                "Net Profit ($M)": "FLOAT"
            }
        }
    }
}
