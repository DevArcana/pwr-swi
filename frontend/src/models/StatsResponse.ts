export interface Stats {
    white_win: number;
    black_win: number;
    draw: number;
}

interface Move extends Stats {
    move: string;
    popularity: number;
}

export default interface StatsResponse {
    overall: Stats;
    moves: Move[];
}
