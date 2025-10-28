from airflow import DAG
from datetime import datetime, timedelta
from airflow.operators.bash import BashOperator

default_args= {
	'owner': 'abhijith',
	'retries': 5,
	'retry_delay': timedelta(minutes=2)
}

with DAG(
	dag_id='our_first_one',
	description='sample DAG',
	default_args=default_args,
	start_date=datetime(2021, 7, 29, 2),
	schedule='@daily'
) as dag:
	task1 = BashOperator(
		task_id = 'first_task',
		bash_command = "echo hello"
	)
	
	task1