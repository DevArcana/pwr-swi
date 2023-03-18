import io

from flask import Flask, render_template, redirect, request
from flask_wtf import FlaskForm, CSRFProtect
from wtforms import StringField
from wtforms.validators import DataRequired
from typesense_api import get_collection
import chess
import chess.pgn
import chess.svg
import urllib

app = Flask(__name__)
app.secret_key = 'this is a security vulnerability'
csrf = CSRFProtect(app)


class SearchForm(FlaskForm):
    query = StringField('query', validators=[DataRequired()])


@app.route("/", methods=['GET', 'POST'])
def welcome():
    form = SearchForm()
    if form.validate_on_submit():
        q = urllib.parse.quote_plus(form.query.data, safe='')
        return redirect(f'/search?q={q}')
    return render_template('welcome.html', form=form)


@app.route("/search")
def search():
    form = SearchForm()

    if form.validate_on_submit():
        q = urllib.parse.quote_plus(form.query.data, safe='')
        return redirect(f'/search?q={q}')

    q = request.args.get('q') or ''
    form.query.data = q

    search_parameters = {
        'q': q,
        'query_by': 'mainline_moves'
    }
    ts_collection = get_collection()
    result = ts_collection.documents.search(search_parameters)
    hits = result['hits']

    for hit in hits:
        moves = hit['document']['mainline_moves']
        game = chess.pgn.read_game(io.StringIO(moves))
        board = game.end().board()
        hit['board_svg'] = chess.svg.board(board)

    results = {'total_count': result['found'], 'duration': result['search_time_ms'], 'out_of': result['out_of'], 'hits': hits}

    return render_template('search.html', form=form, results=results)
