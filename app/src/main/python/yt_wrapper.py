import yt_dlp

def get_video_info(url):
    ydl_opts = {
        'quiet': True,
        'skip_download': True,
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=False)
        return {
            "title": info.get("title"),
            "url": info.get("url"),
            "duration": info.get("duration"),
            "thumbnail": info.get("thumbnail"),
            "cookies": info.get("cookies"),
        }