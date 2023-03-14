## Technology

This project was made with Python 3.11.2.
To manage Python versions, [pyenv](https://github.com/pyenv/pyenv) was used.
Dependency management was done using [venv](https://docs.python.org/3/library/venv.html).

The search engine backend used is [typesense](https://typesense.org/).
It can be run using [Docker](https://www.docker.com/) via the provided [docker-compose](https://docs.docker.com/compose/) file.

The datasets used were sourced from [Lichess](https://database.lichess.org/#standard_games).

The recommended IDE for developing and running this project is [PyCharm](https://www.jetbrains.com/pycharm/).

## Running the application

1. Download datasets `./redownload_datasets.sh` 
2. Install python `3.11.2`
2. Install dependencies `python -m pip install -r requirements.txt`
3. Start typesense server `docker-compose up`
4. Run the program `python main.py`