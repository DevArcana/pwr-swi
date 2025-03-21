import { useState } from "react";
import SearchResultsResponse from "../models/SearchResultsResponse.ts";
import StatsResponse from "../models/StatsResponse.ts";

const useGamesApi = () => {
    const [results, setResults] = useState<SearchResultsResponse | null>(null);
    const [stats, setStats] = useState<StatsResponse | null>(null);

    const search = (query: string) => {
        fetch(`/api/games?q=${query}`)
            .then((x) => x.ok && x.json())
            .then((x) => x as SearchResultsResponse)
            .then((x) => setResults(x))
            .catch(() => setResults(null));

        fetch(`/api/stats?q=${query}`)
            .then((x) => x.ok && x.json())
            .then((x) => x as StatsResponse)
            .then((x) => setStats(x))
            .catch(() => setStats(null));
    };

    return { results, stats, search };
};

export default useGamesApi;
