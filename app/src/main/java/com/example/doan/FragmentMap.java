package com.example.doan;


import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.doan.interfaceFragment.OnMapFragmentInteractionListener;
import com.example.doan.model.Pothole;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;


import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.MultiPoint;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.Polygon;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.Layer;
import com.mapbox.maps.extension.style.layers.LayerUtils;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;

import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.utils.BitmapUtils;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;

import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.doan.Mapbox;
public class FragmentMap extends Fragment
        implements PermissionsListener {
    private MapView mapView;
    private PermissionsManager permissionsManager;

    private List<Pothole> potholes ;
    //Các thành phần của layout
    private FloatingActionButton navigationButton;
    private FloatingActionButton mylocationButton;
    private PlaceAutocomplete placeAutocomplete;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private SearchResultsView searchResultsView;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    //Các thành phần trên map Mapbox
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String sysbolIconId = "symbolIconId";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    //Các thành phần trên map navigation Mapbox
    MapboxNavigation navigation;
    private CarmenContext home;
    private CarmenContext work;
    //route
    private MapboxDirections client;
    double distance;

    public FragmentMap() {
        super(R.layout.fragment_map);
    }

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            mylocationButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };


    private PermissionsListener permissionsListener;
    //Lắng nghe các quyền cần để sử dụng cho ứng dụng
    @Override
    public void onExplanationNeeded(@NonNull List<String> list) {

    }

    //Kết quả cấp phép được gọi khi người dùng có cho phép hoặc từ chối cấp phép hay không, xử lí hành động sau khi ứng dụng yêu cầu cấp phép tiếp theo
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {

        } else {

        }
    }



    public void setMylocationButton() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(10.0).build());
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPulsingEnabled(true);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        mylocationButton.setOnClickListener(viewButton -> {

            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).addOnMoveListener(onMoveListener);
            mylocationButton.hide();
        });
    }

    public void setSearchET() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

            }

            @Override
            public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                ignoreNextQueryUpdate = true;
                searchET.setText(placeAutocompleteSuggestion.getName());
                searchResultsView.setVisibility(View.GONE);
            }

            @Override
            public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {

            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Tạo các điểm pothole cố định trên map
    private void createPointPothole(){

        potholes = new ArrayList<>();
        Pothole pothole1 = new Pothole(10.866296, 106.8017840);
        Pothole pothole2 = new Pothole(10.867776, 106.803824);
        Pothole pothole3 = new Pothole(10.867172, 106.803109);
        Pothole pothole4 = new Pothole(10.873592, 106.803434);
        Pothole pothole5 = new Pothole(10.874991, 106.805494);
        Pothole pothole6 = new Pothole(10.872040, 106.798741);
        potholes.add(pothole1);
        potholes.add(pothole2);
        potholes.add(pothole3);
        potholes.add(pothole4);
        potholes.add(pothole5);
        potholes.add(pothole6);
    }

    private PointAnnotationManager pointPotholeAnnotationManager ;
    //Quản lí các điểm pothole trên map
    public void addPotholeToMap(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pothole_pin);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointPotholeAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);


            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(bitmap)
                    .withPoint(Point.fromLngLat(10.866296, 106.8017840));
            pointPotholeAnnotationManager.create(pointAnnotationOptions);



    }

    private void setclickOnMap(){


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pin);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
        ViewAnnotationManager viewAnnotationManager = mapView.getViewAnnotationManager();

        addOnMapClickListener(mapView.getMapboxMap(), pointDestination -> {
            // Xóa tất cả các annotation
            pointAnnotationManager.deleteAll();

            // Tạo annotation mới tại vị trí click
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(resizedBitmap)
                    .withPoint(pointDestination);
            pointAnnotationManager.create(pointAnnotationOptions);

            // Gọi hàm getRoute với point
            getRoute(pointDestination);


            return false;
        });
    }


    private OnMapFragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnMapFragmentInteractionListener){
            listener = (OnMapFragmentInteractionListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
        createPointPothole();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapbox);
        mylocationButton = (FloatingActionButton) view.findViewById(R.id.mylocationButton);
        mylocationButton.hide();
        navigationButton = (FloatingActionButton) view.findViewById(R.id.navigationButton);
        searchET = (TextInputEditText) view.findViewById(R.id.searchET);
        searchResultsView = (SearchResultsView) view.findViewById(R.id.search_results_view);
        return view;
    }


    //để hiển thị vị trí hiện tại và các thành phần của vị trí Mapbox thông qua location component
    //thông qua thư viện location của mapbox
    //nhưng ở đây sử dụng thư viện LocationComponentPlugin để kích hoạt vị trí người dùng
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));
        //Nhấp chuột vào đây sẽ thực hiện hiển thị navigation map
        navigationButton.setOnClickListener( viewButton -> {

        });

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {


                navigationButton.setOnClickListener(viewButton ->{
                    listener.onMapButtonClicked(new FragmentMapNavigation());
                } );

                addPotholeToMap();

                setMylocationButton();

                setSearchET();

                setclickOnMap();


                Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_layer_symbol,null);
                Bitmap bitmap = BitmapUtils.INSTANCE.getBitmapFromDrawable(drawable);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);

                //Add the symbol layer icon to map for future use
                style.addImage(sysbolIconId,bitmap);

//                //Create an empty GeoJson source using the empty feature collection.
//                setUpSource(style);
//
//                //Set up a new symbol layer for displaying the searched location's feature coordinates.
//                setupLayer(style);
//
//                initSource(style);
//
//                initlayer(style)
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getRoute(Point destination) {

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());

        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();

                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                MapboxDirections.Builder builder = MapboxDirections.builder();
                RouteOptions routeOptions = RouteOptions.builder()
                        .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                        .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                        .coordinatesList(Arrays.asList(origin, destination))
                        .waypointsPerRoute(true)
                        .alternatives(true)
                        .build();

                builder.routeOptions(routeOptions);
                builder.accessToken(getString(R.string.mapbox_access_token));

                builder.build().enqueueCall(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        DirectionsResponse directionsResponse = response.body();
                        DirectionsRoute currentRoute = directionsResponse.routes().get(0);


                        LineString lineString = LineString.fromPolyline(currentRoute.geometry(),PRECISION_6);

                        Feature routeFeature = Feature.fromGeometry(lineString);
                        // Make a toast which displays the route's distance
                        String encodedPolyline = currentRoute.geometry();
                        Toast.makeText(getContext(), String.format("Route distance: %.2f meters", currentRoute.distance()), Toast.LENGTH_SHORT).show();
                        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,style -> {
                            GeoJsonSource.Builder route = new GeoJsonSource.Builder(ROUTE_SOURCE_ID).feature(routeFeature);

                            SourceUtils.addSource(style,route.build());
                            LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID)
                                    .lineColor("#3b9ddd")
                                    .lineWidth(5F);
                            LayerUtils.addLayer(style,routeLayer);
                        });

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
