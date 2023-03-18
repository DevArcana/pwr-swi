import typesense_api

if __name__ == '__main__':
    typesense_api.ensure_chess_collection_exists()
    typesense_api.load_all_datasets()
