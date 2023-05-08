import requests
import os
import chess.pgn
from datetime import datetime
import multiprocessing


def load_all_datasets():
    processes: list[multiprocessing.Process] = []
    for file in os.listdir('datasets'):
        process = multiprocessing.Process(target=fill_chess_collection, args=(f'datasets/{file}',))
        processes.append(process)
        process.start()
    for process in processes:
        process.join()


def fill_chess_collection(path):
    print(f'loading dataset: {path}')
    docs = []
    counter = 0
    with open(path) as file:
        while True:
            game = chess.pgn.read_game(file)
            if game is None:
                requests.post("http://localhost:8983/api/collections/chess/update?commit=true", json=docs)
                break

            if (len(docs) + 1) % 10001 == 0:
                requests.post("http://localhost:8983/api/collections/chess/update?commit=true", json=docs)
                docs = []

            date = game.headers.get('UTCDate')  # 2012.12.31
            time = game.headers.get('UTCTime')  # 23:04:12
            white_elo = game.headers.get('WhiteElo')
            black_elo = game.headers.get('BlackElo')

            game = {
                'id': game.headers.get('Site').split('/')[-1],  # [Site "https://lichess.org/j1dkb5dw"]
                'link': game.headers.get('Site'),
                'timestampUtc': int(datetime.strptime(f'{date} {time}', '%Y.%m.%d %H:%M:%S').timestamp()),
                'event': game.headers.get('Event'),
                'white': game.headers.get('White'),
                'black': game.headers.get('Black'),
                'whiteElo': int(white_elo) if white_elo != "?" else 0,
                'blackElo': int(black_elo) if black_elo != "?" else 0,
                'opening': game.headers.get('Opening'),
                'moves': str(game.mainline_moves())
            }

            docs.append(game)
            counter = counter + 1
            if counter % 10000 == 0:
                print(f"{path}: {counter}")


if __name__ == "__main__":

    requests.post("http://localhost:8983/api/collections", json={
        "create": {
            "name": "chess",
            "numShards": 1,
            "replicationFactor": 1
        }
    })

    requests.post("http://localhost:8983/api/collections/chess/schema", json={
      "add-field": [
        {"name": "link", "type": "string"},
        {"name": "timestampUtc", "type": "pint"},
        {"name": "event", "type": "string"},
        {"name": "white", "type": "string"},
        {"name": "black", "type": "string"},
        {"name": "whiteElo", "type": "pint"},
        {"name": "blackElo", "type": "pint"},
        {"name": "opening", "type": "string"},
        {"name": "moves", "type": "string"}
      ]
    })

    load_all_datasets()

