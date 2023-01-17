//
//  MySwiftModule.swift
//  MyProjectCode
//
//  Created by hieu.vu on 1/17/23.
//

import Foundation
import UIKit
import Alamofire

@objc(MySwiftModule)
class MySwiftModule: RCTEventEmitter {
    @objc
    func fetchData(_ url: String) -> Void {
      AF.request(url).responseJSON { response in
          // handle response data here
        print(response.value);
        
        self.sendEvent(withName: "FetchData", body: response.value)
      }
    }
  
  override func supportedEvents() -> [String]! {
    return ["FetchData"]
  }
}


