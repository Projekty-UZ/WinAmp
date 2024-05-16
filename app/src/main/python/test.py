import pytube
from os.path import dirname, join
from com.chaquo.python import Python

def main():
    files_dir=str(Python.getPlatform().getApplication().getFilesDir())
    yt=pytube.YouTube("https://www.youtube.com/watch?v=tvTRZJ-4EyI")
    stream=yt.streams.get_audio_only()
    stream.download(dirname(files_dir),'audio.mp4')