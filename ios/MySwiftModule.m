//
//  MySwiftModule.m
//  MyProjectCode
//
//  Created by hieu.vu on 1/17/23.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(MySwiftModule, RCTEventEmitter)
  RCT_EXTERN_METHOD(fetchData: url (String) url)
@end
