import chessPiece from "../assets/chess_piece.svg";
import Search from "../components/Search.tsx";
import { useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import SearchResult from "../components/SearchResult.tsx";
import useGamesApi from "../hooks/useGamesApi.ts";

const ResultsPage = () => {
    const [params, setParams] = useSearchParams();
    const [query, setQuery] = useState(params.get("q") || "");

    const { results, stats, search } = useGamesApi();

    useEffect(() => {
        search(query);
        // run only once
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const onSearch = (q: string) => {
        setParams({ q });
        search(q);
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
                    {results && results.hits.map((game) => <SearchResult key={game.link} game={game} />)}
                </main>
                <aside className="">{JSON.stringify(stats)}</aside>
            </div>
        </>
    );
};

export default ResultsPage;
