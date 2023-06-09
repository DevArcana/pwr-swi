export default interface ChessGameResponse {
    black: string;
    white: string;

    link: string;
    event: string;

    mainline_moves: string;
    opening: string;

    positions: string[];

    termination: string;
    timestamp_utc: string;
}
