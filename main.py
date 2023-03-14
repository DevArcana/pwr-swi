import typesense_api
from flask import Flask, request

if __name__ == '__main__':
    typesense_api.ensure_chess_collection_exists()
    typesense_api.fill_chess_collection()
    exit(0)

app = Flask(__name__)


@app.route('/')
def hello_world():
    q = request.args.get('q')
    if q is None:
        return f'<p>{q}</p>'
    return f'<p>nothing</p>'
