#!/bin/bash

rm -rf datasets
mkdir datasets
cd datasets || exit

for i in $(seq -w 1 12); do
  curl https://database.lichess.org/standard/lichess_db_standard_rated_2013-"$i".pgn.zst -o lichess_db_standard_rated_2013-"$i".pgn.zst
  zstd -d lichess_db_standard_rated_2013-"$i".pgn.zst
  rm lichess_db_standard_rated_2013-"$i".pgn.zst
done

date >downloaded_at.txt
