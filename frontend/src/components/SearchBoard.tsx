import React, { useState } from "react";
import { Dialog } from "@headlessui/react";
import { Squares2X2Icon } from "@heroicons/react/24/solid";
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

const SearchBoard: React.FC = () => {
    const [open, setOpen] = useState(false);
    const [board, setBoard] = useState<Board | null>(null);
    return (
        <>
            <button onClick={() => setOpen(true)}>
                <Squares2X2Icon width={16} />
            </button>
            <Dialog open={open} onClose={() => setOpen(false)} className="relative z-50">
                <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
                <div className="fixed inset-0 flex items-center justify-center p-4">
                    <Dialog.Panel className="mx-auto rounded bg-white p-3">
                        <chess-board
                            ref={(e: Board) => setBoard(e)}
                            position="start"
                            draggable-pieces
                            spare-pieces
                        ></chess-board>
                        <button className="p-3" onClick={() => board?.clear()}>
                            clear
                        </button>
                        <button onClick={() => board?.start()}>start</button>
                    </Dialog.Panel>
                </div>
            </Dialog>
        </>
    );
};

export default SearchBoard;
