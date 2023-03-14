import typesense
import chess.pgn
from datetime import datetime

typesense_client = typesense.Client({
    'nodes': [{
        'host': 'localhost',
        'port': '8108',
        'protocol': 'http'
    }],
    'api_key': 'xyz',
    'connection_timeout_seconds': 2
})

collection_name = 'chess'

schema = {
    'name': collection_name,
    'fields': [
        {'name': 'timestamp_utc', 'type': 'int32'},  # [UTCDate "2012.12.31"] [UTCTime "23:04:12"]
        {'name': 'event', 'type': 'string'},  # [Event "Rated Classical game"]
        {'name': 'white', 'type': 'string'},  # [White "BFG9k"]
        {'name': 'black', 'type': 'string'},  # [Black "mamalak"]
        {'name': 'opening', 'type': 'string'},  # [Opening "French Defense: Normal Variation"]
        {'name': 'termination', 'type': 'string'},  # [Termination "Normal"]
        {'name': 'mainline_moves', 'type': 'string'},  # 1. e4 e6 2. d4 b6 3. a3 Bb7 4. Nc3 Nh6 5. Bxh6 gxh6 6. Be2...
    ],
    'default_sorting_field': 'timestamp_utc'
}


def ensure_chess_collection_exists():
    names = [x['name'] for x in typesense_client.collections.retrieve()]

    if collection_name in names:
        typesense_client.collections[collection_name].delete()

    typesense_client.collections.create(schema)


def fill_chess_collection():
    with open('datasets/lichess_db_standard_rated_2013-01.pgn') as file:
        counter = 0
        while True:
            game = chess.pgn.read_game(file)
            if game is None:
                break
            counter = counter + 1
            if counter > 100:
                break

            date = game.headers.get('UTCDate')  # 2012.12.31
            time = game.headers.get('UTCTime')  # 23:04:12
            ts_game = {
                'id': game.headers.get('Site'),  # [Site "https://lichess.org/j1dkb5dw"]
                'timestamp_utc': int(datetime.strptime(f'{date} {time}', '%Y.%m.%d %H:%M:%S').timestamp()),
                'event': game.headers.get('Event'),
                'white': game.headers.get('White'),
                'black': game.headers.get('Black'),
                'opening': game.headers.get('Opening'),
                'termination': game.headers.get('Termination'),
                'mainline_moves': str(game.mainline_moves()),
            }

            collection = typesense_client.collections[collection_name]
            collection.documents.upsert(ts_game)
            print(f'Upsert game #{counter} {ts_game["id"]}')


def get_collection():
    return typesense_client.collections[collection_name]
