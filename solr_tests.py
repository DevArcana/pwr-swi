import requests
from timeit import default_timer as timer
from concurrent.futures import ThreadPoolExecutor
import chess.pgn
from datetime import datetime


def query(n):
    start = timer()
    moves = '"1. e4 e5 2. Nf3 Nc6 3. Bb5"'
    requests.get(f"http://localhost:8983/solr/chess/query?q=moves:{moves}&sort=timestampUtc%20desc")
    end = timer()
    return end - start


def create_index():
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


def get_10000():
    with open("./datasets/lichess_db_standard_rated_2013-01.pgn") as file:
        docs = []
        while True:
            game = chess.pgn.read_game(file)

            if len(docs) == 10000:
                return docs

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


def test_10000(docs):
    requests.post("http://localhost:8983/api/collections/chess/update?commit=true", json=docs)


if __name__ == "__main__":
    # create_index()
    # print(test_10000()) # ~900ms, ~20% CPU usage, ram usage not changed
    # print(query(1)) # ~70ms

    # with ThreadPoolExecutor(max_workers=500) as pool:
    #     # 500+: error
    #     # 300: 1.01s, 8% CPU
    #     # 100: 0.4s, 8%
    #     # 50: 0.17s, 8%
    #     # 10: 0.04s, 8%
    #     r = list(pool.map(query, range(10000)))
    #     print(f"Average time: {sum(r) / len(r):.2f} seconds")

    x = [0, 322000, 493000, 673000, 943000, 1183000, 1393000, 1573000, 1921000, 2000000, 2210000, 2346000, 2526000,
         2706000, 2870000, 3200000, 3328000, 3386000]
    y = [0, 292, 451, 660, 941, 1060, 1320, 1470, 1800, 1860, 1960, 2080, 2330, 2630, 2630, 2680, 2970, 3020]
