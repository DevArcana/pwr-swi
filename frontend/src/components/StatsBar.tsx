import { Stats } from "../models/StatsResponse.ts";

interface Props {
    heading: string;
    stats: Stats;
}

const StatsBar: React.FC<Props> = ({ heading, stats }) => {
    const whiteWinPercentage = Math.round(stats.white_win * 100);
    const drawPercentage = Math.round(stats.draw * 100);
    const blackWinPercentage = Math.round(stats.black_win * 100);

    return (
        <div className="flex-col justify-center p-3 gap-3">
            <h3 className="text-center text-lg">{heading}</h3>
            <div className="flex">
                <div className="bg-white p-2" style={{ flexGrow: whiteWinPercentage }}>
                    {whiteWinPercentage}%
                </div>
                <div className="bg-gray-300 p-2" style={{ flexGrow: drawPercentage }}>
                    {drawPercentage}%
                </div>
                <div className="bg-black text-white p-2" style={{ flexGrow: blackWinPercentage }}>
                    {blackWinPercentage}%
                </div>
            </div>
        </div>
    );
};

export default StatsBar;
