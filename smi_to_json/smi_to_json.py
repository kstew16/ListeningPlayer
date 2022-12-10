import pypyodbc as odbc
import re
import json
import os
import logging


# smi 파일에서 태그를 분류하여 각 태그가 key가 되는 dict로 나타냅니다.
def extract_sync_data_from_smi(dir_path, file_name, search_option="ENCC"):
    
    # smi 파일 open
    with open(dir_path + "\\" + file_name, 'r', encoding='utf8') as f:
        text = f.read()
        
        if(search_option == ("KRCC" or "krcc")):
            regular_expression = "(<sync start=[0-9]+><p class=krcc>)(&nbsp;|\n(?!<sync start=[0-9]+><p class=krcc>).*|\n)|(<SYNC Start=[0-9]+><P Class=KRCC>)(&nbsp;|\n(?!<SYNC Start=[0-9]+><P Class=KRCC>).*|\n)"
        else:
            regular_expression = "(<sync start=[0-9]+><p class=encc>)(&nbsp;|\n(?!<sync start=[0-9]+><p class=encc>).*|\n)|(<SYNC Start=[0-9]+><P Class=ENCC>)(&nbsp;|\n(?!<SYNC Start=[0-9]+><P Class=ENCC>).*|\n)"

        searched_sync = re.findall(regular_expression, text)
        
        sync_list = []

    for t in searched_sync:
        
        if(t[0] == ""):
            
            # startTime 추출
            regular_expression = "[0-9]+"
            startTime = int(re.findall(regular_expression, t[2])[0])
            
            # text 추출
            if(t[3] == "&nbsp;"):
                text = t[3]
            else:
                if(t[3] =="\n"):
                    text = None
                else:
                    text = t[3][1:]
            
            # 딕셔너리 형태로 리스트에 담아줌
            sync_list.append({"startTime": startTime, "text": text})
        
        else:
            # startTime 추출
            regular_expression = "[0-9]+"
            startTime = int(re.findall(regular_expression, t[0])[0])
            
            # text 추출
            if(t[1] == "&nbsp;"):
                text = t[1]
            else:
                if(t[1] =="\n"):
                    text = None
                else:
                    text = t[1][1:]
            
            # 딕셔너리 형태로 리스트에 담아줌
            sync_list.append({"startTime": startTime, "text": text})
        
    return sync_list
            



#######################################################################################################################
#######################################################################################################################

# 괄호 위치 인덱스 찾기
def find_bracket_index(line):
    
    # < 위치와 > 위치 저장할 리스트 생성
    bracket_start_i = []
    bracket_end_i = []
        
    # < 위치 전부 찾아서 저장        
    search_start_i = 0        
    found_index = line.find('<', search_start_i)
        
    while(found_index != -1):
        bracket_start_i.append(found_index)
        search_start_i = found_index + 1
            
        found_index = line.find('<', search_start_i)
        
    # > 위치 전부 찾아서 저장
    search_start_i = 0        
    found_index = line.find('>', search_start_i)
        
    while(found_index != -1):
        bracket_end_i.append(found_index)
        search_start_i = found_index + 1
            
        found_index = line.find('>', search_start_i)

    return bracket_start_i, bracket_end_i
       
######## 태그 분류기 (기본형, full tag만 있어야함, full tag 1개 이상 필요) (예시 참고) ########
    
# full tag                                       형태 분류 가능
    
# full tag - no tag                              형태 분류 가능
# no tag - full tag                              형태 분류 가능
    
# no tag - full tag - no tag                     형태 분류 가능
    
# full tag - no tag - full tag - no tag          형태 분류 가능
# no tag - full tag - no tag - full tag          형태 분류 가능
    
# no tag - full tag - no tag - full tag - no tag 형태 분류 가능

def extract_tag_normal(line, bracket_start_i, bracket_end_i):
        
    classified_line = []
        
    # 처음에 태그로 시작하지 않으면
    if(bracket_start_i[0] != 0):
        classified_line.append((line[:bracket_start_i[0]], "no tag"))
        
    for i in range(len(bracket_start_i)):
        classified_line.append((line[bracket_start_i[i]:bracket_end_i[i]+1], "full tag"))

        # 태그와 태그 사이에 태그가 아닌 것이 있으면
        if(i+1 < len(bracket_start_i)):
            if(bracket_end_i[i]+1 != bracket_start_i[i+1]):
                classified_line.append((line[bracket_end_i[i]+1:bracket_start_i[i+1]], "no tag"))
        
    # 끝에 태그로 끝나지 않으면
    if(bracket_end_i[-1] < len(line)-1):
        classified_line.append((line[bracket_end_i[-1]+1:], "no tag"))
  
    return classified_line

# 전체 tag 분류기
def extract_tag(line):
    
    classified_line = []
    
    # 괄호 인덱스 찾기
    bracket_start_i, bracket_end_i = find_bracket_index(line)
        
    # 괄호 0개
    if((len(bracket_start_i) == 0) & (len(bracket_end_i) == 0)):
        classified_line.append((line, "no tag"))
        
    # 괄호 1개 < 
    elif((len(bracket_start_i) == 1) & (len(bracket_end_i) == 0)):
            
        # 처음에 태그로 시작하지 않으면
        if(bracket_start_i[0] != 0):
            classified_line.append((line[:bracket_start_i[0]], "no tag"))
                
        classified_line.append((line[bracket_start_i[0]:], "right open tag"))
        
    # 괄호 1개 >
    elif((len(bracket_start_i) == 0) & (len(bracket_end_i) == 1)):
            
        classified_line.append((line[:bracket_end_i[0]+1], "left open tag"))
            
        # 끝에 태그로 끝나지 않으면
        if(bracket_end_i[0] != len(line)-1):
            classified_line.append((line[bracket_end_i[0]+1:], "no tag"))

    # 괄호 2개 <>
    elif((len(bracket_start_i) == 1) & (len(bracket_end_i) == 1) & (bracket_start_i[0] < bracket_end_i[0])):
            
        classified_line = extract_tag_normal(line, bracket_start_i, bracket_end_i)

    # 괄호 2개 ><
    elif((len(bracket_start_i) == 1) & (len(bracket_end_i) == 1) & (bracket_start_i[0] > bracket_end_i[0])):
            
        classified_line.append((line[:bracket_end_i[0]+1], "left open tag"))       
        classified_line.append((line[bracket_end_i[0]+1:bracket_start_i[0]], "no tag"))   
        classified_line.append((line[bracket_start_i[0]:], "right open tag"))
            
            
    # 괄호 3개 이상 홀수
    elif((len(bracket_start_i) + len(bracket_end_i)) % 2 == 1):
        
        if(len(bracket_start_i) > len(bracket_end_i)):
                
            classified_line += extract_tag_normal(line[:bracket_start_i[-1]], bracket_start_i[:-1], bracket_end_i)
            
            classified_line.append((line[bracket_start_i[-1]:], "right open tag"))
                
        elif(len(bracket_start_i) < len(bracket_end_i)):
                
            classified_line.append((line[:bracket_end_i[0]+1], "left open tag"))
                
            bias = -1 * (bracket_end_i[0] + 1)
            classified_line += extract_tag_normal(line[bracket_end_i[0]+1:], [x + bias for x in bracket_start_i], [y + bias for y in bracket_end_i[1:]])

    # 괄호 3개 이상 짝수
    elif((len(bracket_start_i) + len(bracket_end_i)) % 2 == 0):

        if(bracket_start_i[0] < bracket_end_i[0]):
                
            classified_line = extract_tag_normal(line, bracket_start_i, bracket_end_i)
                
        elif(bracket_start_i[0] > bracket_end_i[0]):
                
            classified_line.append((line[:bracket_end_i[0]+1], "left open tag"))
                        
            bias = -1 * (bracket_end_i[0] + 1)
            classified_line += extract_tag_normal(line[bracket_end_i[0]+1:bracket_start_i[-1]], [x + bias for x in bracket_start_i[:-1]], [y + bias for y in bracket_end_i[1:]])
            
            classified_line.append((line[bracket_start_i[-1]:], "right open tag"))

    return classified_line

#######################################################################################################################

DRIVER_NAME = 'SQL SERVER'
# SERVER_NAME = 'DESKTOP-K0079A2\\SQLEXPRESS'
SERVER_NAME = 'DESKTOP-UI75720\\SQLEXPRESS'
DATABASE_NAME = 'english'

# mssql 서버 연결을 위한 세팅
def connect_to_mssql_server():
    connection_string = f"""
        DRIVER={{{DRIVER_NAME}}};
        SERVER={{{SERVER_NAME}}};
        DATABASE={DATABASE_NAME};
        Trust_Connection=yes;
    """
    conn = odbc.connect(connection_string)
    cursor = conn.cursor()
    
    return conn, cursor



# text에서 <br> (또는 <br /> 등)의 형태만 제거
def remove_br_from_text(text):
    
    classified_text = extract_tag(text)

    # <br> index 구하기
    br_index_list = []

    for i in range(len(classified_text)):

        # full tag가 있을 경우
        if(classified_text[i][1] == "full tag"):
            
            # 해당 full tag가 <br로 시작하는 경우
            if(classified_text[i][0][:3] == "<br"):
                
                br_index_list.append(i) 

    # <br> index 활용하여 br만 빼고 나머지 병합
    converted_text = ""    
    for i in range(len(classified_text)):
        
        if(i not in br_index_list):
            converted_text += classified_text[i][0]
            converted_text += " "
    
    return converted_text



# text에서 특수 기호 및 줄바꿈(<br> 또는 <br /> 등) 기호를 확인하여 변환하고, 이를 이용해 word_list를 추출하여 반환합니다.
def convert_text_and_extract_word_list(text):

    # br만 제거
    text = remove_br_from_text(text)
    
    # 영어(소문자, 대문자), 숫자, 공백, 작은 따옴표(') 제외하고 모두 제거
    text = re.sub(r"""[^a-zA-Z0-9\s']""", "", text)    
    
    word_list = text.split()

    return word_list

# word list를 입력으로 넣어주면, 각 word 별 text에서의 startIndex를 구하여 startIndex list를 반환합니다. 
def get_startIndex_list(text, word_list):
    
    startIndex_list = []
    text_to_search = text
    checked_index = 0

    for word in word_list:
        startIndex_list.append(checked_index + text_to_search.find(word))
        
        text_to_search = text_to_search[len(word)+1:]
        checked_index += (len(word) + 1)
        
    return startIndex_list

# 작은 따옴표 ' 가 쿼리문에 들어갈 때, ''로 변환되어야 하므로, 단어에 '가 나오면 ''가 되도록 변환합니다.
def convert_quote_in_word(word):
    
    converted_word = ""
    alphabet_list = []

    for i in range(len(word)):
        
        # 작은 따옴표(')일 경우 작은 따옴표 2개('')로 바꿔줌
        if(word[i] == "'"):
            alphabet_list.append("'")
            alphabet_list.append("'")
        
        # 작은 따옴표 아닐 경우 pass
        else:
            alphabet_list.append(word[i])
        
    for alphabet in alphabet_list:
        converted_word += alphabet
        
    return converted_word

# 현재 단어로 DB에 query를 보내 결과 값을 받아옴
def get_query_result(word, cursor):

    # Query문 작성
    sql_statement = f'''
                    SELECT Word, meaningKor, Level 
                    FROM Words
                    WHERE Word = LOWER('{word}') OR Word IN(
                        SELECT Word
                        FROM Variants
                        WHERE Variant = LOWER('{word}'))
                    '''
    # Query 실행
    cursor.execute(sql_statement)
    # Query 결과  
    query_result = cursor.fetchone()
    
    return query_result


def get_extras_from_text(text, cursor):
    
    extras = []
    
    if(text != "&nbsp;" and text != None):
    
        # text에서 특수 기호 및 줄바꿈(<br> 또는 <br /> 등) 기호를 확인하여 변환하고, 이를 이용해 word_list를 추출하여 반환합니다.
        word_list = convert_text_and_extract_word_list(text)

        # word list를 입력으로 넣어주면, 원래 text에서의 각 word 별 startIndex를 구하여 startIndex list를 반환합니다. 
        startIndex_list = get_startIndex_list(text, word_list)
        # 작은 따옴표 ' 가 쿼리문에 들어갈 때, ''로 변환되어야 하므로, 단어에 '가 나오면 ''가 되도록 변환합니다.
        converted_word_list = list(map(convert_quote_in_word, word_list))
        
        for i in range(len(word_list)):
            
            # 현재 단어로 DB에 query를 보내 결과 값을 받아옴
            query_result = get_query_result(converted_word_list[i], cursor)
            
            # 반환된 값이 있으면
            if(query_result != None):
                
                Word = query_result[0]
                meaningKor = query_result[1]
                Level = query_result[2]
                    
                extra = {"type": 'WordItem',
                        "properties": {"variant": word_list[i],
                                    "original": Word,
                                    "startIndex": startIndex_list[i],
                                    "order": i,
                                    "type": 'word',
                                    "level": Level,
                                    "meaning": meaningKor}}
            # 반환된 값이 없으면    
            else:
                extra = {"type": 'WordItem',
                    "properties": {"variant": word_list[i],
                                "original": None,
                                "startIndex": startIndex_list[i],
                                "order": i,
                                "type": 'word',
                                "level": 0,
                                "meaning": None}}
                
            extras.append(extra)
        
    return extras

# mssql 서버 연결 해제
def disconnect_to_mssql_server(conn):
    conn.close()



def convert_syncs_data(syncs_list):
    
    # 데이터 변환 (endTime, extras 추가)
    converted_syncs_list = []
        
    # mssql 서버 연결을 위한 세팅
    conn, cursor = connect_to_mssql_server()

    for i in range(len(syncs_list)):
            
        if(i != len(syncs_list)-1):
            converted_syncs_list.append({"text": syncs_list[i]["text"],
                                        "startTime": syncs_list[i]["startTime"],
                                        "endTime": syncs_list[i+1]["startTime"],
                                        "extras": get_extras_from_text(syncs_list[i]["text"], cursor)})
        
        else:
            converted_syncs_list.append({"text": syncs_list[i]["text"],
                                        "startTime": syncs_list[i]["startTime"],
                                        "endTime": 9223372036854775807,
                                        "extras": get_extras_from_text(syncs_list[i]["text"], cursor)})
                
    # mssql 서버 연결 해제
    disconnect_to_mssql_server(conn)

    return converted_syncs_list



# 추출한 데이터를 json 형식에 맞게 변환하거나, 추가합니다.
def convert_extracted_data(extracted_sync_data):
    
    converted_data = {"subtitles": [{"head": {},
                                     "body": {}}]}
    
    # head
    converted_data["subtitles"][0]["head"] = {"language": "CONVERTED",
                                              "title": None,
                                              "attributes": {}}

    # body - syncs
    converted_syncs_data = convert_syncs_data(extracted_sync_data)
    
    converted_data["subtitles"][0]["body"]["syncs"] = converted_syncs_data
    
    return converted_data

#######################################################################################################################
#######################################################################################################################

# json 형식에 맞게 변환한 데이터를 기반으로 json 파일을 생성합니다.
def create_json_from_converted_data(dir_path, file_name, converted_data):
    
    json_object = converted_data

    with open(dir_path + "\\" + file_name, 'w', encoding='UTF8') as f:
        json.dump(json_object, f, indent=4, ensure_ascii=False)

#######################################################################################################################
#######################################################################################################################

# smi 파일 1개를 json으로 변환하여 저장합니다.
def smi_to_json(smi_dir_path, smi_file_name, json_dir_path=None, json_file_name=None):
    
    if(json_dir_path == None):
        
        if("json" not in os.listdir(smi_dir_path[:-4])):
            os.mkdir(smi_dir_path[:-4] + "\json")
        
        json_dir_path = smi_dir_path[:-4] + "\json"
    
    if(json_file_name == None):
        json_file_name = smi_file_name[:-4] + ".json"
    
    # smi 파일에서 데이터를 추출합니다. (search_option은 smi 파일에서 가져올 자막의 class를 선택)
    extracted_data = extract_sync_data_from_smi(smi_dir_path, smi_file_name, search_option="KRCC")

    # 추출한 데이터를 json 형식에 맞게 변환하거나, 추가합니다.
    converted_data = convert_extracted_data(extracted_data)

    # json 형식에 맞게 변환한 데이터를 기반으로 json 파일을 생성합니다.
    create_json_from_converted_data(json_dir_path, json_file_name, converted_data)


#######################################################################################################################
#######################################################################################################################
#######################################################################################################################

# smi 폴더 내 모든 smi 파일들을 json으로 변환하여 json 폴더 내에 저장합니다.
def smi_to_json_all(dir_path):
    
    if("smi" not in os.listdir(dir_path)):
        os.mkdir(dir_path + "\smi")
    if("json" not in os.listdir(dir_path)):
        os.mkdir(dir_path + "\json")
        
    smi_dir_path = dir_path + "\smi"
    json_dir_path = dir_path + "\json"
    
    smi_file_list = os.listdir(dir_path + "\smi")
    
    if(len(smi_file_list) == 0):
        print("변환할 smi 파일이 없습니다!")
    else:
        smi_file_name_list = list(map(lambda x: x[:-4], smi_file_list))

        for file_name in smi_file_name_list:
            smi_file_name = file_name + ".smi"
            json_file_name = file_name + ".json"
            
            print(f">>>>> {file_name} 변환 중")
            
            # smi 파일을 json으로 변환하여 저장합니다.
            try:
                smi_to_json(smi_dir_path, smi_file_name, json_dir_path, json_file_name)
            except:
                print(f"      {file_name} 변환 중 오류 발생 !")
                print("")
                logging.error("Exception occurred", exc_info=True)
                print("")
                print("--------------------------------------------------------------------------------------------------")
            else:
                print(f"      {file_name} 변환 완료")
                print("--------------------------------------------------------------------------------------------------")


#######################################################################################################################
#######################################################################################################################
#######################################################################################################################


# main

dir_path = "C:\\Users\\kamco\\Desktop\\smi_to_json"
smi_to_json_all(dir_path)

