import chessPiece from "../assets/chess_piece.svg";
import Search from "../components/Search.tsx";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const [query, setQuery] = useState("");
    const navigate = useNavigate();

    const onSearch = (q: string) => {
        navigate(`search?q=${q}`);
    };

    return (
        <>
            <div className="h-screen grid grid-rows-[1fr_auto_1fr]">
                <header className="flex justify-center items-center gap-12">
                    <img className="h-1/3" src={chessPiece} alt="Chess Piece" />
                    <h1 className="text-6xl">Search</h1>
                </header>
                <div className="flex justify-center">
                    <Search query={query} setQuery={setQuery} onSearch={onSearch} />
                </div>
            </div>
        </>
    );
};

export default Home;
