#import <RCTAppDelegate.h>
#import <UIKit/UIKit.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface AppDelegate : RCTAppDelegate

@end

@interface MySwiftModuleCode : RCTEventEmitter <RCTBridgeModule>

@end
