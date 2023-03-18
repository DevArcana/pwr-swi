from flask import Flask, render_template, redirect, request
from flask_wtf import FlaskForm, CSRFProtect
from wtforms import StringField
from wtforms.validators import DataRequired
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

    items = ['a', 'b', 'c']
    results = {'total_count': 999, 'duration': 127.52334, 'results': items}

    return render_template('search.html', form=form, results=results)
