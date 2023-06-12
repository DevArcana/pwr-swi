import ChessGameResponse from "../models/ChessGameResponse.ts";
import { useRef, useState } from "react";
import { Chessboard, ChessboardHandle } from "./Chessboard.tsx";
import {
    ChevronDoubleLeftIcon,
    ChevronDoubleRightIcon,
    ChevronLeftIcon,
    ChevronRightIcon,
    MagnifyingGlassIcon,
} from "@heroicons/react/24/solid";
import { Disclosure } from "@headlessui/react";

interface Props {
    game: ChessGameResponse;
    onSearch: (fen: string) => void;
}

const SearchResult: React.FC<Props> = ({ game, onSearch }) => {
    const board = useRef<ChessboardHandle>(null);
    const [positionIndex, setPositionIndex] = useState(game.positions.length - 1);
    const prevPositionAvailable = positionIndex - 1 >= 0;
    const nextPositionAvailable = positionIndex + 1 < game.positions.length;
    const prevPosition = () => {
        if (prevPositionAvailable) {
            setPositionIndex(positionIndex - 1);
        }
    };
    const nextPosition = () => {
        if (nextPositionAvailable) {
            setPositionIndex(positionIndex + 1);
        }
    };
    const firstPosition = () => {
        setPositionIndex(0);
    };
    const lastPosition = () => {
        setPositionIndex(game.positions.length - 1);
    };
    const searchCurrentBoard = () => {
        const fen = board.current?.fen();
        if (fen) {
            onSearch(fen);
        }
    };
    return (
        <div className="bg-white shadow rounded p-2 flex flex-col gap-3">
            <div className="flex gap-3">
                <div className="w-36 shadow p-1 bg-gray-200 rounded flex flex-col gap-1">
                    <Chessboard
                        className="w-full -mb-[18px]"
                        hideNotation={true}
                        position={game.positions[positionIndex]}
                        ref={board}
                    />
                    <div className="flex items-center justify-evenly">
                        <button onClick={firstPosition} className="hover:text-gray-800 disabled:text-gray-500 z-0">
                            <ChevronDoubleLeftIcon width={16} />
                        </button>
                        <button
                            disabled={!prevPositionAvailable}
                            onClick={prevPosition}
                            className="hover:text-gray-800 disabled:text-gray-500 z-0"
                        >
                            <ChevronLeftIcon width={16} />
                        </button>
                        <p className="text-xs text-gray-500">
                            {positionIndex + 1}/{game.positions.length}
                        </p>
                        <button
                            disabled={!nextPositionAvailable}
                            onClick={nextPosition}
                            className="hover:text-gray-800 disabled:text-gray-500 z-0"
                        >
                            <ChevronRightIcon width={16} />
                        </button>
                        <button onClick={lastPosition} className="hover:text-gray-800 disabled:text-gray-500 z-0">
                            <ChevronDoubleRightIcon width={16} />
                        </button>
                        <button onClick={searchCurrentBoard} className="hover:text-gray-800 disabled:text-gray-500 z-0">
                            <MagnifyingGlassIcon width={16} />
                        </button>
                    </div>
                </div>
                <a className="bg-gray-200 shadow-md rounded hover:bg-gray-300 p-3 flex-auto" href={game.link}>
                    <h1 className="text-xs text-gray-500 flex justify-between">
                        <span>#{game.link.split("/")[3]}</span>
                        <span>avg elo: {Math.round((game.white_elo + game.black_elo) / 2)}</span>
                    </h1>
                    <h2 className="text-sm">{game.event}</h2>
                    <h3>
                        {game.white} (white) {game.result} {game.black} (black)
                    </h3>
                    <hr className="h-px bg-gray-300 my-2 border-0" />
                    <p className="text-sm">Opening: {game.opening}</p>
                    <p className="text-sm">Termination: {game.termination}</p>
                </a>
            </div>
            <Disclosure>
                <Disclosure.Button className="text-xs bg-gray-200 hover:bg-gray-300 rounded p-1">
                    Moves
                </Disclosure.Button>
                <Disclosure.Panel className="text-xs text-gray-500">{game.mainline_moves}</Disclosure.Panel>
            </Disclosure>
        </div>
    );
};

export default SearchResult;
