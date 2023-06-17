import React, { Fragment } from "react";
import { MagnifyingGlassIcon } from "@heroicons/react/24/solid";
import { Combobox, Transition } from "@headlessui/react";
import SearchBoard from "./SearchBoard.tsx";
import useOpeningsApi from "../hooks/useOpeningsApi.ts";

interface Props {
    query: string;
    setQuery: (query: string) => void;
    onSearch: (query: string) => void;
}

const fengex = /(((?:[rnbqkpRNBQKP1-8]+\/){7})[rnbqkpRNBQKP1-8]+)/;

const Search: React.FC<Props> = ({ query, setQuery, onSearch }) => {
    const { openings, getOpenings } = useOpeningsApi();

    const filteredOpenings =
        query === ""
            ? []
            : openings?.filter((opening) => {
                  return opening.toLowerCase().includes(query.toLowerCase());
              }) || [];

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
            <Combobox value={query} onChange={(query) => setQuery(query)}>
                <div className="relative w-full">
                    <span className="absolute flex justify-center items-center">
                        <button type="submit" className="p-1 focus:outline-none focus:shadow-outline">
                            <MagnifyingGlassIcon width={16} />
                        </button>
                    </span>
                    <Combobox.Input
                        onChange={(event) => {
                            setQuery(event.target.value);
                            getOpenings(event.target.value);
                        }}
                        type="search"
                        className="text-sm pl-7 focus:outline-none w-full"
                        value={query}
                        placeholder="e4 e5"
                        autoComplete="off"
                    />
                    <Transition
                        as={Fragment}
                        leave="transition ease-in duration-100"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <Combobox.Options className="absolute bg-white flex-col gap-2 max-h-64 overflow-auto">
                            {filteredOpenings.map((opening) => (
                                <Combobox.Option key={opening} value={opening}>
                                    {({ active }) => (
                                        <li
                                            className={`${
                                                active ? "bg-blue-500 text-white" : "bg-white text-black"
                                            } p-1`}
                                        >
                                            {opening}
                                        </li>
                                    )}
                                </Combobox.Option>
                            ))}
                        </Combobox.Options>
                    </Transition>
                </div>
            </Combobox>
            <SearchBoard fen={currentFen} onSearch={handleSearch} />
        </form>
    );
};

export default Search;
