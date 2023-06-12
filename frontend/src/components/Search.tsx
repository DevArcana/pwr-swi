import React from "react";
import { MagnifyingGlassIcon } from "@heroicons/react/24/solid";
import SearchBoard from "./SearchBoard.tsx";

interface Props {
    query: string;
    setQuery: (query: string) => void;
    onSearch: (query: string) => void;
}

const fengex = /(((?:[rnbqkpRNBQKP1-8]+\/){7})[rnbqkpRNBQKP1-8]+)/;

const Search: React.FC<Props> = ({ query, setQuery, onSearch }) => {
    const handleSearch = (fen: string) => {
        let q = query;

        if (q.match(fengex)) {
            q = q.replace(fengex, fen);
        } else {
            q = `${q} ${fen}`;
        }

        setQuery(q);
        onSearch(q);
    };

    const currentFen = (query.match(fengex) ?? [undefined])[0];

    return (
        <form
            className="relative w-96 bg-white rounded p-2 flex items-center"
            onSubmit={(event) => {
                event.preventDefault();
                onSearch(query);
            }}
        >
            <span className="absolute flex justify-center items-center">
                <button type="submit" className="p-1 focus:outline-none focus:shadow-outline">
                    <MagnifyingGlassIcon width={16} />
                </button>
            </span>
            <input
                type="search"
                className="text-sm pl-7 focus:outline-none w-full"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="e4 e5"
                autoComplete="off"
            />
            <SearchBoard fen={currentFen} onSearch={handleSearch} />
        </form>
    );
};

export default Search;
