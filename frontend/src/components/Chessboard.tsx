import { forwardRef, useEffect } from "react";
import "chessboard-element";

/*
ADAPTED FROM https://stackblitz.com/edit/react-chess-board?file=index.js
 */

declare global {
    // eslint-disable-next-line @typescript-eslint/no-namespace
    namespace JSX {
        interface IntrinsicElements {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            "chess-board": any;
        }
    }
}

interface Props {
    className?: string;
    sparePieces?: boolean;
    hideNotation?: boolean;
    draggablePieces?: boolean;
    position?: string;
}

export interface ChessboardHandle {
    clear: () => void;
    start: () => void;
    fen: () => string;
    setPosition: (fen: string) => void;
}

export const Chessboard = forwardRef<ChessboardHandle, Props>((props, ref) => {
    useEffect(() => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        const board = ref.current as ChessboardHandle;

        if (board && props.position) {
            board.setPosition(props.position);
        }
    }, [ref, props.position]);

    return (
        <chess-board
            ref={ref}
            className={props.className}
            spare-pieces={props.sparePieces}
            draggable-pieces={props.draggablePieces}
            hide-notation={props.hideNotation}
        ></chess-board>
    );
});
