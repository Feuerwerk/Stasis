Pod::Spec.new do |spec|
  spec.name         = 'Stasis'
  spec.version      = '1.0.0'
  spec.summary      = 'An RPC Framework for Java, .Net and Cocoa.'
  spec.homepage     = 'https://github.com/Feuerwerk/Stasis'
  spec.license     = { :type => 'MIT', :file => 'LICENSE' }
  spec.author       = { 'Christian Fruth' => 'christian.fruth@boxx-it.de' }
  spec.source       = { :git => 'https://github.com/Feuerwerk/Stasis.git', :tag => spec.version.to_s }
  spec.platform    = :ios, '6.0'
  spec.ios.deployment_target = '6.0'
  spec.source_files = 'stasis-ios/Stasis/*.{h,m}'
  spec.public_header_files = 'stasis-ios/Stasis/Stasis.h', 'stasis-ios/Stasis/RemoteConnection.h', 'stasis-ios/Stasis/HttpRemoteConnection.h', 'stasis-ios/Stasis/*Serializer.h', 'stasis-ios/Stasis/*Exception.h', 'stasis-ios/Stasis/StasisAsyncServiceWrapper.h'
  spec.resources    = 'stasis-ios/StasisResources/**/*.strings'
  spec.frameworks   = 'CoreFoundation'
  spec.library      = 'z'
  spec.requires_arc = true
  spec.dependency   'Kryo'
  spec.dependency   'JodaTime'
end