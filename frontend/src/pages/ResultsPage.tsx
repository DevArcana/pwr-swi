import chessPiece from "../assets/chess_piece.svg";
import Search from "../components/Search.tsx";
import { useSearchParams } from "react-router-dom";
import { useState } from "react";
import SearchResult from "../components/SearchResult.tsx";
import ChessGame from "../models/ChessGame.ts";

const ResultsPage = () => {
    const [params, setParams] = useSearchParams();
    const [query, setQuery] = useState(params.get("q") || "");
    const [games] = useState<ChessGame[]>([{ id: 1 }, { id: 2 }, { id: 3 }]);
    const onSearch = (q: string) => {
        setParams({ q });
    };

    return (
        <>
            <div className="h-screen grid grid-rows-[auto_1fr] grid-cols-2">
                <header className="flex items-center gap-3 p-3 col-span-2">
                    <img className="h-16" src={chessPiece} alt="Chess Piece" />
                    <h1 className="text-2xl mr-6">Search</h1>
                    <Search query={query} setQuery={setQuery} onSearch={onSearch} />
                </header>
                <main className="bg-red-400 flex flex-col gap-3 p-3">
                    {games.map((game) => (
                        <SearchResult game={game} />
                    ))}
                </main>
                <aside className="">Side results go here</aside>
            </div>
        </>
    );
};

export default ResultsPage;
