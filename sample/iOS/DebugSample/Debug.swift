//
//  Debug.swift
//  DebugSample
//
//  Created by Dimitry Ivanov on 9/6/24.
//

import Foundation
import KMPDebug

fileprivate typealias _Debug = KMPDebug.Debug

final class Debug {

    static func configure(block: @escaping (DebugConfiguration) -> Void) {
        DebugCompanion.shared.configure(configuration: block)
    }

    private static let shared = Debug()

    private let debug: _Debug

    private init(_ debug: _Debug? = nil) {
        self.debug = debug ?? DebugCompanion.shared
    }

    static func tag(tag: String) -> Debug {
        return .init(shared.debug.tag(tag: tag))
    }

    #if DEBUG
    static func v(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .verbose, message: message)
    }

    func v(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .verbose, message: message)
    }

    static func d(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .debug, message: message)
    }

    func d(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .debug, message: message)
    }

    static func i(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .info, message: message)
    }

    func i(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .info, message: message)
    }

    static func w(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .warn, message: message)
    }

    func w(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .warn, message: message)
    }

    static func e(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .error, message: message)
    }

    func e(
        file: String = #file,
        line: Int = #line,
        function: String = #function,
        _ message: Any?...
    ) {
        log(file: file, line: line, function: function, level: .error, message: message)
    }

    func log(
        file: String,
        line: Int,
        function: String,
        level: DebugLogLevel,
        message: Array<Any?>
    ) {
        debug.log(
            level: level,
            message: Self.createMessageWithCaller(
                file: file,
                line: line,
                function: function,
                message: message
            )
        )
    }

    static func log(
        file: String,
        line: Int,
        function: String,
        level: DebugLogLevel,
        message: Array<Any?>
    ) {
        shared.debug.log(
            level: level,
            message: createMessageWithCaller(
                file: file,
                line: line,
                function: function,
                message: message
            )
        )
    }

    private static func createMessageWithCaller(
        file: String,
        line: Int,
        function: String,
        message: Array<Any?>
    ) -> KotlinArray<AnyObject> {
        let caller = "\(function)(\(cleanCallerFile(file) ?? ""):\(line))"
        let array: Array<Any?> = .init(arrayLiteral: caller) + message
        return messageArray(array)
    }

    private static func cleanCallerFile(_ file: String?) -> String? {
        // /path/MyFile.swift
        guard let file = file else { return nil }

        let prefix = {
            if let index = file.lastIndex(of: "/") {
                return file.index(after: index)
            }
            return file.startIndex
        }()
        let suffix = file.lastIndex(of: ".") ?? file.endIndex

        return String(file[prefix..<suffix])
    }

    #else
    static func v(_ message: Any?...) {
        shared.debug.v(message: messageArray(*message))
    }

    func v(_ message: Any?...) {
        debug.v(message: Self.messageArray(*message))
    }

    static func d(_ message: Any?...) {
        shared.debug.d(message: messageArray(message))
    }

    func d(_ message: Any?...) {
        debug.d(message: Self.messageArray(message))
    }

    static func i(_ message: Any?...) {
        shared.debug.i(message: messageArray(message))
    }

    func i(_ message: Any?...) {
        debug.i(message: Self.messageArray(message))
    }

    static func w(_ message: Any?...) {
        shared.debug.w(message: messageArray(message))
    }

    func w(_ message: Any?...) {
        debug.w(message: Self.messageArray(message))
    }

    static func e(_ message: Any?...) {
        shared.debug.e(message: messageArray(message))
    }

    func e(_ message: Any?...) {
        debug.e(message: Self.messageArray(message))
    }
    #endif

    private static func messageArray(_ message: Array<Any?>) -> KotlinArray<AnyObject> {
        return KotlinArray(
            size: .init(message.count),
            init: {
                if let value = message[$0.intValue] {
                    return String(describing: value) as NSString
                } else {
                    return nil
                }
            }
        )
    }
}
