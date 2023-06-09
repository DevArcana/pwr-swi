import React from "react";
import { MagnifyingGlassIcon } from "@heroicons/react/24/solid";
import SearchBoard from "./SearchBoard.tsx";

interface Props {
    query: string;
    setQuery: (query: string) => void;
    onSearch: (query: string) => void;
}

const Search: React.FC<Props> = ({ query, setQuery, onSearch }) => {
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
            <SearchBoard />
            {/*<Dialog.Root>*/}
            {/*    <Dialog.Trigger asChild>*/}
            {/*        <button className="flex justify-center items-center p-1 hover:text-gray-700">*/}
            {/*            <Squares2X2Icon width={16} />*/}
            {/*        </button>*/}
            {/*    </Dialog.Trigger>*/}
            {/*    <Dialog.Portal>*/}
            {/*        <Dialog.Overlay className="bg-black opacity-30 fixed inset-0" />*/}
            {/*        <Dialog.Content className="bg-white rounded p-3 fixed top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]">*/}
            {/*            <div>*/}

            {/*            </div>*/}
            {/*        </Dialog.Content>*/}
            {/*    </Dialog.Portal>*/}
            {/*</Dialog.Root>*/}
        </form>
    );
};

export default Search;
