import React, { useEffect, useState } from "react";
import { MagnifyingGlassIcon, TransparencyGridIcon } from "@radix-ui/react-icons";
import * as Dialog from "@radix-ui/react-dialog";
import "chessboard-element";

/*
ADAPTED FROM https://stackblitz.com/edit/react-chess-board?file=index.js
 */

declare global {
    // eslint-disable-next-line @typescript-eslint/no-namespace
    namespace JSX {
        interface IntrinsicElements {
            "chess-board": any;
        }
    }
}

interface Board {
    clear: () => void;
    start: () => void;
}

interface Props {
    query: string;
    setQuery: (query: string) => void;
    onSearch: (query: string) => void;
}

const Search: React.FC<Props> = ({ query, setQuery, onSearch }) => {
    const [board, setBoard] = useState<Board | null>(null);

    // you can remove this, it's only to show available methods
    useEffect(() => {
        if (board) {
            board.clear();
            board.start();
        }
    }, [board]);

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
                    <MagnifyingGlassIcon className="text-gray-500" />
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
            <Dialog.Root>
                <Dialog.Trigger asChild>
                    <button className="flex justify-center items-center p-1 hover:text-gray-700">
                        <TransparencyGridIcon />
                    </button>
                </Dialog.Trigger>
                <Dialog.Portal>
                    <Dialog.Overlay className="bg-black opacity-30 fixed inset-0" />
                    <Dialog.Content className="bg-white rounded p-3 fixed top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]">
                        <div>
                            <chess-board
                                ref={(e: Board) => setBoard(e)}
                                position="start"
                                draggable-pieces
                                spare-pieces
                            ></chess-board>
                        </div>
                    </Dialog.Content>
                </Dialog.Portal>
            </Dialog.Root>
        </form>
    );
};

export default Search;
