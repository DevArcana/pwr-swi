import React, { useRef, useState } from "react";
import { Dialog } from "@headlessui/react";
import { Squares2X2Icon } from "@heroicons/react/24/solid";
import "chessboard-element";
import { Chessboard, ChessboardHandle } from "./Chessboard.tsx";

const SearchBoard: React.FC = () => {
    const [open, setOpen] = useState(false);
    const board = useRef<ChessboardHandle>(null);
    return (
        <>
            <button onClick={() => setOpen(true)}>
                <Squares2X2Icon width={16} />
            </button>
            <Dialog open={open} onClose={() => setOpen(false)} className="relative z-50">
                <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
                <div className="fixed inset-0 flex items-center justify-center p-4">
                    <Dialog.Panel className="mx-auto rounded bg-white p-3">
                        <Chessboard ref={board} position={"start"} draggablePieces={true} sparePieces={true} />
                        <button className="p-3" onClick={() => board.current?.clear()}>
                            clear
                        </button>
                        <button onClick={() => board.current?.start()}>start</button>
                    </Dialog.Panel>
                </div>
            </Dialog>
        </>
    );
};

export default SearchBoard;
