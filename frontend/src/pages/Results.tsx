import chessPiece from "../assets/chess_piece.svg";
import Search from "../components/Search.tsx";
import { useSearchParams } from "react-router-dom";
import { useState } from "react";

const Results = () => {
    const [params, setParams] = useSearchParams();
    const [query, setQuery] = useState(params.get("q") || "");

    const onSearch = (q: string) => {
        setParams({ q });
    };

    return (
        <>
            <div className="h-screen grid grid-rows-[1fr_auto_1fr]">
                <header className="flex justify-center items-center gap-12">
                    <img className="h-1/3" src={chessPiece} alt="Chess Piece" />
                    <h1 className="text-6xl">Results</h1>
                </header>
                <div className="flex items-center justify-center flex-col">
                    <div>Query: {params.get("q")}</div>
                    <Search query={query} setQuery={setQuery} onSearch={onSearch} />
                </div>
            </div>
        </>
    );
};

export default Results;
