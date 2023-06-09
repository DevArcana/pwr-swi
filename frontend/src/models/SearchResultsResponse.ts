import ChessGameResponse from "./ChessGameResponse.ts";

export default interface SearchResultsResponse {
    count: number;
    page: number;
    total_pages: number;
    hits: ChessGameResponse[];
}
