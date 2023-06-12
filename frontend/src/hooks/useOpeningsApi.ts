import {useState} from "react";

const useOpeningsApi = () => {
    const [openings, setOpenings] = useState<string[] | null>(null);

    const getOpenings = (query: string) => {
        fetch(`/api/openings?q=${query}`)
                .then((x) => x.ok && x.json())
                .then((x) => x as string[])
                .then((x) => setOpenings(x))
                .catch(() => setOpenings(null));
    };

    return { openings, getOpenings };
};

export default useOpeningsApi;
