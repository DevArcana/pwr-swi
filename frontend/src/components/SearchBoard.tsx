import React, { useRef, useState } from "react";
import { Dialog } from "@headlessui/react";
import {
    ArrowPathRoundedSquareIcon,
    MagnifyingGlassCircleIcon,
    Squares2X2Icon,
    TrashIcon,
} from "@heroicons/react/24/solid";
import "chessboard-element";
import { Chessboard, ChessboardHandle } from "./Chessboard.tsx";

interface Props {
    onSearch: (fen: string) => void;
    fen?: string;
}

const SearchBoard: React.FC<Props> = ({ onSearch, fen }) => {
    const [open, setOpen] = useState(false);
    const board = useRef<ChessboardHandle>(null);
    const searchFen = () => {
        const fen = board.current?.fen();
        if (fen) {
            onSearch(fen);
            setOpen(false);
        }
    };

    return (
        <>
            <button onClick={() => setOpen(true)}>
                <Squares2X2Icon width={16} />
            </button>
            <Dialog open={open} onClose={() => setOpen(false)} className="relative z-50">
                <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
                <div className="fixed inset-0 flex items-center justify-center p-4">
                    <Dialog.Panel className="mx-auto rounded bg-white p-3 w-96 flex flex-col">
                        <Chessboard
                            className="w-full"
                            ref={board}
                            position={fen ?? "start"}
                            draggablePieces={true}
                            sparePieces={true}
                        />
                        <div className="bg-gray-300 rounded -mt-[45px] z-10 p-3 flex justify-evenly items-center">
                            <button className="flex items-center gap-1" onClick={() => board.current?.clear()}>
                                <span>clear</span>
                                <TrashIcon width={24} />
                            </button>
                            <button className="flex items-center gap-1" onClick={searchFen}>
                                <span>search FEN</span>
                                <MagnifyingGlassCircleIcon width={24} />
                            </button>
                            <button className="flex items-center gap-1" onClick={() => board.current?.start()}>
                                <span>reset</span>
                                <ArrowPathRoundedSquareIcon width={24} />
                            </button>
                        </div>
                    </Dialog.Panel>
                </div>
            </Dialog>
        </>
    );
};

export default SearchBoard;
