import React from "react";

interface Props {
    query: string;
    setQuery: (query: string) => void;
    onSearch: (query: string) => void;
}

const Search: React.FC<Props> = ({ query, setQuery, onSearch }) => {
    return (
        <form
            className="relative w-96"
            onSubmit={(event) => {
                event.preventDefault();
                onSearch(query);
            }}
        >
            <span className="absolute inset-y-0 left-0 flex items-center pl-2">
                <button type="submit" className="p-1 focus:outline-none focus:shadow-outline">
                    <svg
                        fill="none"
                        stroke="currentColor"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        viewBox="0 0 24 24"
                        className="w-6 h-6"
                    >
                        <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                    </svg>
                </button>
            </span>
            <input
                type="search"
                className="py-2 text-sm rounded-md pl-10 focus:outline-none w-full"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="e4 e5"
                autoComplete="off"
            />
        </form>
    );
};

export default Search;
