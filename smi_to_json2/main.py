import smi_to_json

## 사용자 환경 변수 세팅 부분

# 1. mssql 서버 이름과 데이터베이스 이름 지정 (계정 연결 필요 시 계정 정보 추가)
driver_name = 'SQL SERVER'
server_name = 'LAPTOP-5UKQG4P2\SQLEXPRESS'
database_name = 'english'
#user_name = 'IT'    # sql server 계정으로 DB 연결 시 사용
#password = '412'    # sql server 계정으로 DB 연결 시 사용

# 2. smi 폴더와 json 폴더가 함께 들어 있는 경로로, 사용자 환경에 맞게 경로 지정
# (smi 폴더: 변환할 smi 파일들이 들어 있는 폴더)
# (json 폴더: 변환된 json 파일들이 들어갈 폴더로, 만약 없더라도 알아서 생성해 줌.)
dir_path = r"E:\My_Documents\Lecture\2022-2 Lectures\IT Convergence\Final Project smi_to_json\smi_to_json"

##################################################################################################################
##################################################################################################################

# mssql 연결
conn, cursor = smi_to_json.connect_to_mssql_server(driver_name, server_name, database_name)
#conn, cursor = smi_to_json.connect_to_mssql_server(driver_name, server_name, database_name, user_name, password) # sql server 계정으로 DB 연결 시 사용

# smi 파일을 json으로 변환하여 저장
# (search_option은 smi 파일에서 p class의 어떤 부분을 찾아서 변환할 지를 선택하는 옵션입니다.)
# (ENCC가 Default 값이지만, 주어진 smi 파일들에서는 영어 자막인데도 불구하고 KRCC로 분류되어 있기 때문에 명시적으로 KRCC 부분을 찾아 변환하라고 지정해 주는 부분입니다.)
smi_to_json.smi_to_json_all(cursor, dir_path, search_option="KRCC")

# mssql 연결 해제
smi_to_json.disconnect_to_mssql_server(conn)
