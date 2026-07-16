import {Wind} from "lucide-react"

export default function  WindyLogo(){

    return (
        <div className="flex gap-4">
            <span className="text-xl font-extrabold tracking-tight text-brand text-emerald-600 text-stroke-1 text-stroke-color-black">WINDY</span>
            <Wind/>
            <span className="text-xl font-extrabold tracking-tight text-brand text-white text-stroke-1 text-stroke-color-black">EXCHANGE</span>

        </div>
    )

}