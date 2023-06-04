import ChessGame from "../models/ChessGame.ts";

interface Props {
    game: ChessGame;
}

const SearchResult: React.FC<Props> = ({ game }) => {
    return <div className="bg-green-500 p-3">{JSON.stringify(game)}</div>;
};

export default SearchResult;
