import pytube
import time
from os.path import dirname, join
from com.chaquo.python import Python

def progress_func(stream, chunk, bytes_remaining):
    progress = (stream.filesize - bytes_remaining) / stream.filesize
    progress_func.progress = int(progress * 100)


progress_func.progress = 0
message = ""


def download_from_yt(link):
    files_dir=join(str(Python.getPlatform().getApplication().getFilesDir()),"musicfiles")
    global message
    try:
        yt = pytube.YouTube(url=link, on_progress_callback=progress_func)
        stream = yt.streams.get_audio_only()
        name = yt.title
        author = yt.author
        filename = f"{int(time.time())}.mp4"
        stream.download(output_path=files_dir, filename=filename)
        message = ["Downloaded",name,author,yt.length,join(files_dir,filename)]
    except Exception as e:
        message = ["Download failed"]
        print(e)


def get_progress():
    return progress_func.progress

def get_message():
    return message