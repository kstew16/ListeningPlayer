# Author: Jaesun Park (Univ. of Seoul)

import socket
from datetime import datetime
import os

MOVIE_STORAGE = r"C:\movie_stroage"
def open_server_socket(host="localhost", port=2004):
    # Author: Jaesun Park (Univ. of Seoul)
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    soc.bind((host, port))
    soc.listen()
    return soc

def sendFile(client: socket.socket, filename: str, size=1024):
    # Author: Jaesun Park (Univ. of Seoul)
    with open(filename, "rb") as f:
        try:
            data = f.read(size)
            while data:
                client.send(data)
                data = f.read(size)
        except Exception as ex:
            print(ex)

    pass

def sendFileList(client:socket.socket):
    files = os.listdir(MOVIE_STORAGE)
    
    for f in files:
        barr = (f+'\n').encode()
        client.send(barr)
    if len(files)==0:
        client.send("None".encode())
 


def main():
    # Author: Jaesun Park (Univ. of Seoul)
    server = open_server_socket(host=socket.gethostbyname(socket.gethostname()), port=3030)
    print("서버 구동 중.. IP 주소 : " + socket.gethostbyname(socket.gethostname()))
    SIZE = 1<<15
    # server.settimeout(1.0)
    print("server ready")
    try:
        # author: Jaesun Park (Univ. of Seoul)
        while True:
            # 1. connect
            c_socket, c_addr = server.accept()

            # 2. receive
            msg = str(c_socket.recv(SIZE), "utf-8")
            print(f"SERVER_RECEIVED: \"{msg}\" from {c_addr} : {datetime.now()}\n")

            # 3. send (file or ack)
            filename = os.path.join(MOVIE_STORAGE, msg)

            if msg=="list": 
                sendFileList(c_socket)
            elif os.path.isfile(filename):
                sendFile(c_socket, filename, SIZE)
            else:
                c_socket.sendall("None".encode())

            # 4. close
            c_socket.close()
            if msg == "quit":
                print("QUIT~")
                break
    except KeyboardInterrupt:
        print("Keyboard Interrupt occured")

    server.close()


main()
