//
//  MySwiftModule.swift
//  MyProjectCode
//
//  Created by hieu.vu on 1/17/23.
//

import Foundation
import UIKit

struct ChannelType {
    var key: String
    var cb: (Any) -> Void
}

@objc(MySwiftModule)
class MySwiftModule: RCTEventEmitter {
  private var dataPubsub: [ChannelType] = []
  
  @objc
  func publicChannel(_ key: String, cb: @escaping (Any) -> Void) -> () -> Void {
      // Perform some operation with the key
    // Look for existing object with matching key
       if let existingObjectIndex = dataPubsub.firstIndex(where: { $0.key == key }) {
           // Replace callback for existing object
         dataPubsub[existingObjectIndex].cb = cb
         
         return { [weak self] in
                     self?.dataPubsub.remove(at: existingObjectIndex)
                 }
       } else {
           // Add new object to array
         dataPubsub.append(ChannelType(key: key, cb: cb))
         
         return { [weak self] in
                     if let index = self?.dataPubsub.firstIndex(where: { $0.key == key }) {
                         self?.dataPubsub.remove(at: index)
                     }
                 }
       }
  }
  
  @objc
  func subscribe(_ key: String, data: Any) {
      if let myObject = dataPubsub.first(where: { $0.key == key }) {
          myObject.cb(data)
      }
  }
  
  override func supportedEvents() -> [String]! {
    return ["subscribe", "publicChannel"]
  }
}


