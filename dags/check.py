from airflow import DAG
from datetime import datetime, timedelta
from airflow.operators.bash import BashOperator

default_args= {
	'owner': 'abhijith',
	'retries': 5,
	'retry_delay': timedelta(minutes=2)
}

with DAG(
	dag_id='second_test',
	description='s DAG',
	default_args=default_args,
	start_date=datetime(2021, 7, 29, 2),
	schedule='@daily'
) as dag:
	task1 = BashOperator(
		task_id = 'second_task',
		bash_command = "echo check"
	)
	
	task1