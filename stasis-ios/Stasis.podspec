Pod::Spec.new do |s|
  s.name         = "Stasis"
  s.version      = "1.0.0"
  s.summary      = "An RPC Framework for Java, .Net and Cocoa."
  s.homepage     = "https://github.com/Feuerwerk/Stasis"
  s.license      = { :type => 'MIT', :file => 'LICENSE' }
  s.author       = { 'Christian Fruth' => 'christian.fruth@boxx-it.de' }
  s.source       = { :git => "https://github.com/Feuerwerk/Stasis.git" }
  #s.platform     = :ios
  s.source_files = 'stasis-ios/Stasis/*.{h,m}'
  s.framework    = "CoreFoundation"
  s.requires_arc = true
end