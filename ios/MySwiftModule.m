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
  RCT_EXTERN_METHOD(publicChannel:(NSString *)key cb:(RCTResponseSenderBlock)cb)
  RCT_EXTERN_METHOD(subscribe:(NSString *)key data:(NSDictionary *)data)
@end
