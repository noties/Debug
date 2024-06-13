//
//  DebugSampleApp.swift
//  DebugSample
//
//  Created by Dimitry Ivanov on 9/6/24.
//

import SwiftUI
import KMPDebug

@main
struct DebugSampleApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
//                .onAppear(perform: configureDebug)
                .onAppear {
                    configureDebug(arg: 1, myOtherArgument: true)
                }
        }
    }

    func configureDebug(arg: Int, myOtherArgument: Bool) {
        Debug.configure {
            $0.iOS

            $0.iOS
                .interceptor {
                    // provider
//                    $0.timestamp { Date().ISO8601Format() }

                    // or pattern
                    $0.timestamp(iso8601Pattern: "yyyy-MM-dd'T'hh:mm:ss.SSS Z")
                }

            // NB! prepend call stack is not implemented by inspecting stack-trace on iOS,
            //  instead a custom swift wrapper is used - `Debug.swift` that has a special debug
            //  version for all log functions that additionally track calling file

            // no-op
            $0.android

            // no-op
            $0.java

            // actually available and will print to console
            $0.kotlin
        }

        Debug.v("Hello", 42)

        Debug.tag(tag: "some-tag")
            .v("World", nil, 180)
    }

//    private func configure(_ configuration: KMPDebug.DebugConfiguration) {
//        let entries = [
//            ("era", "G"),
//            ("year-of-year", "y"),
//            ("day-of-year", "D"),
//            ("month-of-year", "M"),
//            ("day-of-mont", "d"),
//            ("modified-julian-day", "g"),
//            ("quarter-of-year", "Q"),
//            ("quarter-of-year", "q"),
//            ("week-based-year", "Y"),
//            ("week-of-week-based-year", "w"),
//            ("week-of-month", "W"),
//            ("day-of-week", "E"),
//            ("localized-day-of-week", "e"),
//            ("day-of-week-in-month", "F"),
//            ("am-pm-of-day", "a"),
//            ("clock-hour-of-am-pm", "h"),
//            ("hour-of-am-pm", "K"),
//            ("clock-hour-of-day", "k"),
//            ("hour-of-day", "H"),
//            ("minute-of-hour", "m"),
//            ("second-of-minute", "s"),
//            ("fraction-of-second", "S"),
//            ("milli-of-day", "A"),
//            ("nano-of-second", "n"),
//            ("nano-of-day", "N"),
//            ("time-zone-id", "V"),
//            ("generic-time-zone-name", "v"),
//            ("time-zone name", "z"),
//            ("localized zone-offset", "O"),
//            ("zone-offset-Z-for-zero", "X"),
//            ("zone-offset", "x"),
//            ("zone-offset", "Z")
//        ]
//        let pattern = entries
//            .map {
//                "'\($0.0)':" + (["\($0.1)", "\($0.1)\($0.1)", "\($0.1)\($0.1)\($0.1)", "\($0.1)\($0.1)\($0.1)\($0.1)"].joined(separator: "-"))
//            }
//            .joined(separator: " ; ")
//
//        let formatter = DateFormatter()
//        formatter.dateFormat = pattern
//        let s = formatter.string(from: Date())
//        print(s)
//
//        entries
//            .map {
//                "'\($0.0)':" + (["\($0.1)", "\($0.1)\($0.1)", "\($0.1)\($0.1)\($0.1)", "\($0.1)\($0.1)\($0.1)\($0.1)"].joined(separator: "-"))
//            }
//            .forEach {
//                let formatter = DateFormatter()
//                formatter.dateFormat = $0
//                let s = formatter.string(from: Date())
//                print("pattern:'\($0)' date:\(s)")
//
//            }
//
//        configuration.iOS
//            .interceptor {
//                $0.timestamp(iso8601Pattern: "yyyy-MM-dd HH:mm:ss.SSS VV")
//            }
//    }
}
